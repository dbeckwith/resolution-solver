package net.sonaxaton.resolutionsolver;

import org.junit.Assert;

public class LogicParserTest {

    @org.junit.Before
    public void init() {
        LogicClause.showAllParens(true);
    }

    @org.junit.Test
    public void testNegation() throws Exception {
        Assert.assertEquals("((~A) v (B)) > (C)", LogicParser.getInstance().parseLogicExpression("~(A) v B > C").toString());
        Assert.assertEquals("(~((A) v (B))) > (C)", LogicParser.getInstance().parseLogicExpression("~(A v B) > C").toString());
        Assert.assertEquals("~(((A) v (B)) > (C))", LogicParser.getInstance().parseLogicExpression("~(A v B > C)").toString());
        Assert.assertEquals("((~A) v (B)) > (C)", LogicParser.getInstance().parseLogicExpression("~A v B > C").toString());
        Assert.assertEquals("(~A) v ((B) > (C))", LogicParser.getInstance().parseLogicExpression("~A v (B > C)").toString());
    }

    @org.junit.Test
    public void testOrderOfOperations() throws Exception {
        Assert.assertEquals("((A) ^ (B)) ^ (C)", LogicParser.getInstance().parseLogicExpression("A ^ B ^ C").toString());
        Assert.assertEquals("((A) ^ (B)) v (C)", LogicParser.getInstance().parseLogicExpression("A ^ B v C").toString());
        Assert.assertEquals("(A) v ((B) ^ (C))", LogicParser.getInstance().parseLogicExpression("A v B ^ C").toString());
        Assert.assertEquals("((A) v ((B) ^ (C))) v (D)", LogicParser.getInstance().parseLogicExpression("A v B ^ C v D").toString());
        Assert.assertEquals("(A) > ((B) v (C))", LogicParser.getInstance().parseLogicExpression("A > B v C").toString());
        Assert.assertEquals("((A) v (B)) > (C)", LogicParser.getInstance().parseLogicExpression("A v B > C").toString());
        Assert.assertEquals("(A) v ((B) > (C))", LogicParser.getInstance().parseLogicExpression("A v (B > C)").toString());
        Assert.assertEquals("((A) v (B)) > ((C) ^ (D))", LogicParser.getInstance().parseLogicExpression("A v B > C ^ D").toString());
        Assert.assertEquals("((A) > (B)) = (C)", LogicParser.getInstance().parseLogicExpression("A > B = C").toString());
    }

    @org.junit.Test
    public void testParseErrors() throws Exception {
        assertThrown(() -> LogicParser.getInstance().parseLogicExpression("AA > B"), LogicParseException.class);
        assertThrown(() -> LogicParser.getInstance().parseLogicExpression("~~A"), LogicParseException.class);
        assertNotThrown(() -> LogicParser.getInstance().parseLogicExpression("A"), LogicParseException.class);
        assertThrown(() -> LogicParser.getInstance().parseLogicExpression(""), LogicParseException.class);
    }

    private void assertThrown(Runnable method, Class<? extends Exception> type) {
        try {
            method.run();
        }
        catch (Exception e) {
            if (type.isAssignableFrom(e.getClass())) {
                return;
            }
            else {
                Assert.fail("Method did not throw " + type.getName() + ", instead got " + e.getClass().getName());
            }
        }
        Assert.fail("Method did not throw an exception");
    }

    private void assertNotThrown(Runnable method, Class<? extends Exception> type) {
        try {
            method.run();
        }
        catch (Exception e) {
            if (type.isAssignableFrom(e.getClass())) {
                Assert.fail("Method threw " + type.getName());
            }
        }
    }
}