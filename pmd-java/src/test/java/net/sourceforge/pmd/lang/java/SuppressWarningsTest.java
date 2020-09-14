/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSize;

import org.junit.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class SuppressWarningsTest {

    private final JavaParsingHelper java = JavaParsingHelper.WITH_PROCESSING;

    public static class BarRule extends AbstractJavaRule {
        public BarRule() {
            setMessage("fooMessage");
        }

        @Override
        public Object visit(ASTCompilationUnit cu, Object ctx) {
            // Convoluted rule to make sure the violation is reported for the
            // ASTCompilationUnit node
            for (ASTClassOrInterfaceDeclaration c : cu.descendants(ASTClassOrInterfaceDeclaration.class)) {
                if ("bar".equalsIgnoreCase(c.getSimpleName())) {
                    addViolation(ctx, cu);
                }
            }
            return super.visit(cu, ctx);
        }

        @Override
        public String getName() {
            return "NoBar";
        }
    }

    @Test
    public void testClassLevelSuppression() {
        Report rpt = java.executeRule(new FooRule(), TEST1);
        assertSize(rpt, 0);
        rpt = java.executeRule(new FooRule(), TEST2);
        assertSize(rpt, 0);
    }

    @Test
    public void testInheritedSuppression() {
        Report rpt = java.executeRule(new FooRule(), TEST3);
        assertSize(rpt, 0);
    }

    @Test
    public void testMethodLevelSuppression() {
        Report rpt;
        rpt = java.executeRule(new FooRule(), TEST4);
        assertSize(rpt, 1);
    }

    @Test
    public void testConstructorLevelSuppression() {
        Report rpt = java.executeRule(new FooRule(), TEST5);
        assertSize(rpt, 0);
    }

    @Test
    public void testFieldLevelSuppression() {
        Report rpt = java.executeRule(new FooRule(), TEST6);
        assertSize(rpt, 1);
    }

    @Test
    public void testParameterLevelSuppression() {
        Report rpt = java.executeRule(new FooRule(), TEST7);
        assertSize(rpt, 1);
    }

    @Test
    public void testLocalVariableLevelSuppression() {
        Report rpt = java.executeRule(new FooRule(), TEST8);
        assertSize(rpt, 1);
    }

    @Test
    public void testSpecificSuppression() {
        Report rpt = java.executeRule(new FooRule(), TEST9);
        assertSize(rpt, 1);
    }

    @Test
    public void testSpecificSuppressionValue1() {
        Report rpt = java.executeRule(new FooRule(), TEST9_VALUE1);
        assertSize(rpt, 1);
    }

    @Test
    public void testSpecificSuppressionValue2() {
        Report rpt = java.executeRule(new FooRule(), TEST9_VALUE2);
        assertSize(rpt, 1);
    }

    @Test
    public void testSpecificSuppressionValue3() {
        Report rpt = java.executeRule(new FooRule(), TEST9_VALUE3);
        assertSize(rpt, 1);
    }

    @Test
    public void testSpecificSuppressionMulitpleValues1() {
        Report rpt = java.executeRule(new FooRule(), TEST9_MULTIPLE_VALUES_1);
        assertSize(rpt, 0);
    }

    @Test
    public void testSpecificSuppressionMulitpleValues2() {
        Report rpt = java.executeRule(new FooRule(), TEST9_MULTIPLE_VALUES_2);
        assertSize(rpt, 0);
    }

    @Test
    public void testNoSuppressionBlank() {
        Report rpt = java.executeRule(new FooRule(), TEST10);
        assertSize(rpt, 2);
    }

    @Test
    public void testNoSuppressionSomethingElseS() {
        Report rpt = java.executeRule(new FooRule(), TEST11);
        assertSize(rpt, 2);
    }

    @Test
    public void testSuppressAll() {
        Report rpt = java.executeRule(new FooRule(), TEST12);
        assertSize(rpt, 0);
    }

    @Test
    public void testSpecificSuppressionAtTopLevel() {
        Report rpt = java.executeRule(new BarRule(), TEST13);
        assertSize(rpt, 0);
    }

    private static final String TEST1 = "@SuppressWarnings(\"PMD\")\npublic class Foo {}";

    private static final String TEST2 = "@SuppressWarnings(\"PMD\")\npublic class Foo {\n void bar() {\n  int foo;\n }\n}";

    private static final String TEST3 = "public class Baz {\n @SuppressWarnings(\"PMD\")\n public class Bar {\n  void bar() {\n   int foo;\n  }\n }\n}";

    private static final String TEST4 = "public class Foo {\n @SuppressWarnings(\"PMD\")\n void bar() {\n  int foo;\n }\n}";

    private static final String TEST5 = "public class Bar {\n @SuppressWarnings(\"PMD\")\n public Bar() {\n  int foo;\n }\n}";

    private static final String TEST6 = "public class Bar {\n @SuppressWarnings(\"PMD\")\n int foo;\n void bar() {\n  int foo;\n }\n}";

    private static final String TEST7 = "public class Bar {\n int foo;\n void bar(@SuppressWarnings(\"PMD\") int foo) {}\n}";

    private static final String TEST8 = "public class Bar {\n int foo;\n void bar() {\n  @SuppressWarnings(\"PMD\") int foo;\n }\n}";

    private static final String TEST9 = "public class Bar {\n int foo;\n void bar() {\n  @SuppressWarnings(\"PMD.NoFoo\") int foo;\n }\n}";

    private static final String TEST9_VALUE1 = "public class Bar {\n int foo;\n void bar() {\n  @SuppressWarnings(value = \"PMD.NoFoo\") int foo;\n }\n}";

    private static final String TEST9_VALUE2 = "public class Bar {\n int foo;\n void bar() {\n  @SuppressWarnings({\"PMD.NoFoo\"}) int foo;\n }\n}";

    private static final String TEST9_VALUE3 = "public class Bar {\n int foo;\n void bar() {\n  @SuppressWarnings(value = {\"PMD.NoFoo\"}) int foo;\n }\n}";

    private static final String TEST9_MULTIPLE_VALUES_1 = "@SuppressWarnings({\"PMD.NoFoo\", \"PMD.NoBar\"})\npublic class Bar {\n int foo;\n void bar() {\n  int foo;\n }\n}";

    private static final String TEST9_MULTIPLE_VALUES_2 = "@SuppressWarnings(value = {\"PMD.NoFoo\", \"PMD.NoBar\"})\npublic class Bar {\n int foo;\n void bar() {\n  int foo;\n }\n}";

    private static final String TEST10 = "public class Bar {\n int foo;\n void bar() {\n  @SuppressWarnings(\"\") int foo;\n }\n}";

    private static final String TEST11 = "public class Bar {\n int foo;\n void bar() {\n  @SuppressWarnings(\"SomethingElse\") int foo;\n }\n}";

    private static final String TEST12 = "public class Bar {\n @SuppressWarnings(\"all\") int foo;\n}";

    private static final String TEST13 = "@SuppressWarnings(\"PMD.NoBar\")\npublic class Bar {\n}";
}
