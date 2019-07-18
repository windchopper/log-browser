package com.github.windchopper.tools.log.browser.fs;

import com.github.windchopper.common.preferences.PreferencesEntry;
import com.github.windchopper.common.preferences.types.FlatType;
import com.github.windchopper.common.util.SystemProperty;
import com.github.windchopper.tools.log.browser.Globals;
import com.jcraft.jsch.*;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class SftpFileSystem extends RemoteFileSystem implements AutoCloseable {

    private static final Logger logger = Logger.getLogger(SftpFileSystem.class.getName());

    static {
        JSch.setLogger(new JSchLoggerBridge(logger));
    }

    private final static PreferencesEntry<Duration> bufferingDuration = new PreferencesEntry<>(Globals.preferencesStorage, "sftpBufferingDuration",
        new FlatType<>(Duration::parse, Duration::toString), Duration.ofMinutes(5), ChronoUnit.FOREVER.getDuration());

    private final static PreferencesEntry<Integer> bufferingLimit = new PreferencesEntry<>(Globals.preferencesStorage, "sftpBufferingLimit",
        new FlatType<>(Integer::decode, Object::toString), 100, ChronoUnit.FOREVER.getDuration());

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    private final JSch jsch = new JSch();
    private final Lock lock = new ReentrantLock();

    private Session session;

    private final Map<String, Instant> childListInstantMap = new LRUMap<>(bufferingLimit.load());
    private final Map<String, List<SftpFile>> childListMap = new LRUMap<>(bufferingLimit.load());

    public SftpFileSystem(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;

        defaultSecureShellConfigurationDirectory().ifPresent(path -> {
            List<String> identityPathStrings = Stream.of(path.resolve("id_rsa"), path.resolve("id_dsa"), path.resolve("id_ecdsa"))
                .filter(Files::exists)
                .map(Path::toAbsolutePath)
                .map(Path::toString)
                .collect(toList());

            logger.log(Level.FINE, "Using local identities: {}", identityPathStrings);

            try {
                for (String identityPathString : identityPathStrings) {
                    jsch.addIdentity(identityPathString);
                }
            } catch (JSchException thrown) {
                logger.log(Level.WARNING, "Couldn't set identity", thrown);
            }
        });

        defaultSecureShellConfigurationDirectory().ifPresent(path -> {
            try {
                jsch.setKnownHosts(path.resolve("known_hosts").toAbsolutePath().toString());
            } catch (JSchException thrown) {
                logger.log(Level.WARNING, ExceptionUtils.getRootCauseMessage(thrown), thrown);
            }
        });
    }

    private Optional<Path> defaultSecureShellConfigurationDirectory() {
        return SystemProperty.USER_HOME.read(Paths::get)
            .map(path -> path.resolve(".ssh"));
    }

    private Session openSession() throws IOException {
        lock.lock();

        try {
            if (session == null) {
                try {
                    session = jsch.getSession(username, host, port);
                    session.setConfig("StrictHostKeyChecking", "no");
                    session.setPassword(password);
                    session.connect();
                } catch (JSchException thrown) {
                    throw new IOException(thrown);
                }
            }

            return session;
        } finally {
            lock.unlock();
        }
    }

    @Override public RemoteFile root() {
        return new SftpFile("/", true);
    }

    @Override @SuppressWarnings("unchecked") public List<RemoteFile> children(String path) throws IOException {
        try {
            var instant = Instant.now();
            var savedInstant = childListInstantMap.computeIfAbsent(path, missingPath -> Instant.MIN);

            if (Duration.between(savedInstant, instant).compareTo(bufferingDuration.load()) <= 0) {
                return (List) childListMap.get(path);
            }

            ChannelSftp channel = (ChannelSftp) openSession().openChannel("sftp");
            channel.connect();

            try {
                List<ChannelSftp.LsEntry> entries = channel.ls(path);
                Predicate<ChannelSftp.LsEntry> filter = entry -> !StringUtils.equals(entry.getFilename(), ".");

                if (StringUtils.equals(path, "/")) {
                    filter = filter.and(entry -> !StringUtils.equals(entry.getFilename(), ".."));
                }

                var children = entries.stream()
                    .filter(filter)
                    .filter(entry -> !entry.getAttrs().isLink())
                    .map(entry -> new SftpFile((path + "/" + entry.getFilename()).replaceAll("/+", "/"), entry.getAttrs().isDir()))
                    .collect(toList());

                childListInstantMap.put(path, instant);
                childListMap.put(path, children);

                return (List) children;
            } finally {
                channel.disconnect();
            }
        } catch (JSchException | SftpException thrown) {
            throw new IOException(thrown);
        }
    }

    @Override public void close() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

}
