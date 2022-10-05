package org.goodnewstbot;

import io.github.cdimascio.dotenv.Dotenv;

public class Environment {

    public String getEnvValue(String name){
        Dotenv dotenv = Dotenv.load();
        return dotenv.get(name);
    }

}
