package org.example.drools.ttt;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        final KieSession ksession = kc.newKieSession();
   
        new Game(ksession);
        runKSession(ksession);
    }

    public static void runKSession(final KieSession ksession) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(new Runnable() {
            public void run() {
                // run forever
                try {
                    ksession.fireUntilHalt();
                } catch ( Exception e ) {
                    LOG.error("Exception", e);
                }
            }
        });
    }

}