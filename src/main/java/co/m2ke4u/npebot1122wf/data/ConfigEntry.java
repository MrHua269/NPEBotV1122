package co.m2ke4u.npebot1122wf.data;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ConfigEntry {
    private final String username;
    private final String accessToken;
    private final boolean onlineMode;
    private final boolean useProxy;
    private final String proxyAddress;
    private final int proxyPort;
    private final String serverAddress;
    private final int serverPort;
    private final String authMePassword;
    private final boolean enableAuthmeMode;
    private final boolean enableAutoRespawn;
    private final boolean autoReconnect;

    public ConfigEntry(String username,String accessToken,boolean onlineMode,boolean useProxy,String proxyAddress,int proxyPort,String serverAddress,int serverPort,String authMePassword,boolean enableAuthmeMode,boolean enableAutoRespawn,boolean autoReconnect){
        this.username = username;
        this.accessToken = accessToken;
        this.onlineMode = onlineMode;
        this.useProxy = useProxy;
        this.proxyAddress = proxyAddress;
        this.proxyPort = proxyPort;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.authMePassword = authMePassword;
        this.enableAuthmeMode = enableAuthmeMode;
        this.enableAutoRespawn = enableAutoRespawn;
        this.autoReconnect = autoReconnect;
    }

    public boolean isAutoReconnect() {
        return this.autoReconnect;
    }

    public boolean isEnableAutoRespawn() {
        return this.enableAutoRespawn;
    }

    public boolean isEnableAuthmeMode() {
        return this.enableAuthmeMode;
    }

    private static final Gson gson = new Gson();

    public static ConfigEntry readFromFile(File parent,String name){
        File file;
        if (parent!=null){
            file = new File(parent,name);
        }else{
            file = new File(name);
        }
        if (!file.exists()){
            throw new IllegalStateException();
        }
        try {
            return gson.fromJson(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8),ConfigEntry.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveToFile(File parent,String fileName){
        try {
            File file;
            if (parent!=null){
                file = new File(parent,fileName);
            }else{
                file = new File(fileName);
            }
            if (file.exists()){
                file.delete();
            }
            file.createNewFile();
            String jsonContent = gson.toJson(this);
            try (FileWriter writer = new FileWriter(file)){
                writer.write(jsonContent);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isOnlineMode() {
        return this.onlineMode;
    }

    public boolean isUseProxy() {
        return this.useProxy;
    }

    public int getProxyPort() {
        return this.proxyPort;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getProxyAddress() {
        return this.proxyAddress;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public String getServerAddress() {
        return this.serverAddress;
    }

    public String getAuthMePassword(){
        return this.authMePassword;
    }
}
