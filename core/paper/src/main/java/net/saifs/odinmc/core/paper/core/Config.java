package net.saifs.odinmc.core.paper.core;

import java.util.List;
import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Config {

    private Modules modules = new Modules();

    public Modules getModules() {
        return modules;
    }

    @ConfigSerializable
    public static class Modules {

        private ScriptModule script = new ScriptModule();
        private DataModule data = new DataModule();
        private NetworkModule network = new NetworkModule();
        private ShardingModule sharding = new ShardingModule();

        public ShardingModule getSharding() {
            return sharding;
        }

        public ScriptModule getScript() {
            return script;
        }

        public DataModule getData() {
            return data;
        }

        public NetworkModule getNetwork() {
            return network;
        }
    }

    @ConfigSerializable
    public static class NetworkModule {

        private String redisHost = "redis://127.0.0.1:6379";
        private String serverName = "odin";
        private String networkName = "odin";

        public String getRedisHost() {
            return redisHost;
        }

        public String getNetworkName() {
            return networkName;
        }

        public String getServerName() {
            return serverName;
        }
    }

    @ConfigSerializable
    public static class ScriptModule {

        private Map<String, String> modules = Map.of("main", "js/");

        public Map<String, String> getModules() {
            return modules;
        }
    }

    @ConfigSerializable
    public static class ShardingModule {

        private Etcd etcd = new Etcd();
        private Minio minio = new Minio();

        public Etcd getEtcd() {
            return etcd;
        }

        public Minio getMinio() {
            return minio;
        }

        @ConfigSerializable
        public static class Etcd {

            private List<String> urls = List.of("http://127.0.0.1:2379");

            public List<String> getUrls() {
                return urls;
            }
        }

        @ConfigSerializable
        public static class Minio {

            private String url = "http://127.0.0.1";
            private int port = 8123;
            private boolean secure = false;
            private String accessKey = "";
            private String secretKey = "";
            private String persistentBucket = "doodle-persistent";
            private String templateBucket = "doodle-templates";

            public String getPersistentBucket() {
                return persistentBucket;
            }

            public String getTemplateBucket() {
                return templateBucket;
            }

            public String getUrl() {
                return url;
            }

            public int getPort() {
                return port;
            }

            public boolean isSecure() {
                return secure;
            }

            public String getAccessKey() {
                return accessKey;
            }

            public String getSecretKey() {
                return secretKey;
            }
        }
    }

    @ConfigSerializable
    public static class DataModule {

        private Map<String, DatabaseInfo> databases = Map.of("default", new DatabaseInfo());

        public Map<String, DatabaseInfo> getDatabases() {
            return databases;
        }

        @ConfigSerializable
        public static class DatabaseInfo {

            private String url = "jdbc:mysql://localhost/default";
            private String username = "root";
            private String password = "password1";

            public String getUrl() {
                return url;
            }

            public String getUsername() {
                return username;
            }

            public String getPassword() {
                return password;
            }
        }
    }
}
