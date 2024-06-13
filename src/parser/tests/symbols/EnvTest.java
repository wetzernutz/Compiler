package symbols;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnvTest {
    private Env top;

    @BeforeEach
    void setUp() {
        top = new Env(top);
    }

    @Test
    void testNoSymbolGlobal() {
        Assertions.assertNull(top.get("notThere"));
    }

    @Test
    void testSymbolExistGlobal() {
        top.put("a", new Symbol());
        Assertions.assertNotNull(top.get("a"));
    }

    @Test
    void testSymbolExistGlobalGetFromSubEnv() {
        top.put("a", new Symbol());
        for (int i = 0; i < 3; i++) {
            top = new Env(top);
        }
        top.put("b", new Symbol());
        top = new Env(top);

        Assertions.assertNotNull(top.get("a"));
        Assertions.assertNotNull(top.get("b"));
        Assertions.assertNull(top.get("c"));
    }
}