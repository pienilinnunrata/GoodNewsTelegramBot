package org.goodnewstbot;

import io.github.cdimascio.dotenv.Dotenv;

public class Environment {

    public String getEnvValue(String name){
        Dotenv dotenv = Dotenv.configure().filename("config/conf/.env").load();
        return dotenv.get(name);
    }

}
