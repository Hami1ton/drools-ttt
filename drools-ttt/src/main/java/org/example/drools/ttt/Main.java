package org.example.drools.ttt;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class Main {

    public static void main(String[] args) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        final KieSession kSession = kc.newKieSession();
   
        new GameUI(kSession);
    }
}