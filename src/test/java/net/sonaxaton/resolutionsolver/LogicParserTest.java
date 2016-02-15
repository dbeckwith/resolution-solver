package net.sonaxaton.resolutionsolver;

import org.junit.Assert;

public class LogicParserTest {

    @org.junit.Test
    public void testParseLogicExpression() throws Exception {
        LogicClause.showAllParens(true);

        Assert.assertEquals("((~A) v (B)) > (C)", LogicParser.getInstance().parseLogicExpression("~(A) v B > C").toString());
        Assert.assertEquals("(~((A) v (B))) > (C)", LogicParser.getInstance().parseLogicExpression("~(A v B) > C").toString());
        Assert.assertEquals("~(((A) v (B)) > (C))", LogicParser.getInstance().parseLogicExpression("~(A v B > C)").toString());
        Assert.assertEquals("((~A) v (B)) > (C)", LogicParser.getInstance().parseLogicExpression("~A v B > C").toString());
        Assert.assertEquals("(~A) v ((B) > (C))", LogicParser.getInstance().parseLogicExpression("~A v (B > C)").toString());
    }
}