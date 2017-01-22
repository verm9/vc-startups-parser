package vc.startups.parser;

import static org.junit.Assert.*;

/**
 * Created by nonu on 1/22/2017.
 */
public class StartupTest {

    private Startup startup1 = new Startup("ID", "LINK", 10, 10);
    private Startup startup2 = new Startup("ID", "LINK", 1, 10);
    private Startup startup3 = new Startup("ID", "LINK", 23, 55);
    private Startup startup5 = new Startup("ID", "LINK", 15, 27);
    private Startup startup6 = new Startup("ID", "LINK", 7, 28);
    private Startup startup7 = new Startup("ID", "LINK", 0, 28);
    private Startup startup8 = new Startup("ID", "LINK", 0, 0);
    private Startup startup9 = new Startup("ID", "LINK", 1, 0);

    @org.junit.Test
    public void compareTo() throws Exception {
        assertTrue( startup1.compareTo(startup2) > 0 );
        assertTrue( startup2.compareTo(startup3) < 0 );
        assertTrue( startup3.compareTo(startup3) == 0 );
        assertTrue( startup6.compareTo(startup5) < 0 );
        assertTrue( startup6.compareTo(startup7) > 0 );
        assertTrue( startup8.compareTo(startup9) < 0 );
        assertTrue( startup6.compareTo(startup9) < 0 );
    }

}