package org.sahagin.share.srctree;

import java.util.HashMap;
import java.util.Map;

import org.sahagin.share.CommonUtils;
import org.sahagin.share.IllegalDataStructureException;
import org.sahagin.share.srctree.code.Code;
import org.sahagin.share.srctree.code.CodeLine;
import org.sahagin.share.srctree.code.LocalVarAssign;
import org.sahagin.share.srctree.code.SubMethodInvoke;
import org.sahagin.share.srctree.code.TestStep;
import org.sahagin.share.yaml.YamlUtils;
import org.sahagin.share.yaml.YamlConvertException;
import org.sahagin.share.yaml.YamlConvertible;

public class SrcTree implements YamlConvertible {
    private static final String MSG_CLASS_NOT_FOUND = "class not found; key: %s";
    private static final String MSG_METHOD_NOT_FOUND = "method not found; key: %s";
    private static final String MSG_FIELD_NOT_FOUND = "field not found; key: %s";
    private static final String MSG_SRC_TREE_FORMAT_MISMATCH
    = "expected formatVersion is \"%s\", but actual is \"%s\"";

    private TestClassTable rootClassTable = new TestClassTable();
    private TestMethodTable rootMethodTable = new TestMethodTable();
    private TestFieldTable rootFieldTable = new TestFieldTable();
    private TestClassTable subClassTable = new TestClassTable();
    private TestMethodTable subMethodTable = new TestMethodTable();
    private TestFieldTable subFieldTable = new TestFieldTable();

    public TestClassTable getRootClassTable() {
        return rootClassTable;
    }

    public void setRootClassTable(TestClassTable rootClassTable) {
        if (rootClassTable == null) {
            throw new NullPointerException();
        }
        this.rootClassTable = rootClassTable;
    }

    public TestMethodTable getRootMethodTable() {
        return rootMethodTable;
    }

    public void setRootMethodTable(TestMethodTable rootMethodTable) {
        if (rootMethodTable == null) {
            throw new NullPointerException();
        }
        this.rootMethodTable = rootMethodTable;
    }

    public TestFieldTable getRootFieldTable() {
        return rootFieldTable;
    }

    public void setRootFieldTable(TestFieldTable rootFieldTable) {
        if (rootFieldTable == null) {
            throw new NullPointerException();
        }
        this.rootFieldTable = rootFieldTable;
    }

    public TestClassTable getSubClassTable() {
        return subClassTable;
    }

    public void setSubClassTable(TestClassTable subClassTable) {
        if (subClassTable == null) {
            throw new NullPointerException();
        }
        this.subClassTable = subClassTable;
    }

    public TestMethodTable getSubMethodTable() {
        return subMethodTable;
    }

    public void setSubMethodTable(TestMethodTable subMethodTable) {
        if (subMethodTable == null) {
            throw new NullPointerException();
        }
        this.subMethodTable = subMethodTable;
    }

    public TestFieldTable getSubFieldTable() {
        return subFieldTable;
    }

    public void setSubFieldTable(TestFieldTable subFieldTable) {
        if (subFieldTable == null) {
            throw new NullPointerException();
        }
        this.subFieldTable = subFieldTable;
    }

    public void sort() {
        rootClassTable.sort();
        rootMethodTable.sort();
        rootFieldTable.sort();
        subClassTable.sort();
        subMethodTable.sort();
        subFieldTable.sort();
    }

    @Override
    public Map<String, Object> toYamlObject() {
        Map<String, Object> result = new HashMap<String, Object>(8);
        result.put("formatVersion", CommonUtils.formatVersion());
        if (!rootFieldTable.isEmpty()) {
            result.put("rootFieldTable", rootFieldTable.toYamlObject());
        }
        if (!subFieldTable.isEmpty()) {
            result.put("subFieldTable", subFieldTable.toYamlObject());
        }
        if (!rootMethodTable.isEmpty()) {
            result.put("rootMethodTable", rootMethodTable.toYamlObject());
        }
        if (!subMethodTable.isEmpty()) {
            result.put("subMethodTable", subMethodTable.toYamlObject());
        }
        if (!rootClassTable.isEmpty()) {
            result.put("rootClassTable", rootClassTable.toYamlObject());
        }
        if (!subClassTable.isEmpty()) {
            result.put("subClassTable", subClassTable.toYamlObject());
        }
        return result;
    }

    @Override
    public void fromYamlObject(Map<String, Object> yamlObject)
            throws YamlConvertException {
        String formatVersion = YamlUtils.getStrValue(yamlObject, "formatVersion");
        // "*" means arbitrary version (this is only for testing sahagin itself)
        if (!formatVersion.equals("*")
                && !formatVersion.equals(CommonUtils.formatVersion())) {
            throw new YamlConvertException(String.format
                    (MSG_SRC_TREE_FORMAT_MISMATCH, CommonUtils.formatVersion(), formatVersion));
        }

        rootFieldTable = new TestFieldTable();
        Map<String, Object> rootFieldTableYamlObj
        = YamlUtils.getYamlObjectValue(yamlObject, "rootFieldTable", true);
        if (rootFieldTableYamlObj != null) {
            rootFieldTable.fromYamlObject(rootFieldTableYamlObj);
        }

        subFieldTable = new TestFieldTable();
        Map<String, Object> subFieldTableYamlObj
        = YamlUtils.getYamlObjectValue(yamlObject, "subFieldTable", true);
        if (subFieldTableYamlObj != null) {
            subFieldTable.fromYamlObject(subFieldTableYamlObj);
        }

        rootMethodTable = new TestMethodTable();
        Map<String, Object> rootMethodTableYamlObj
        = YamlUtils.getYamlObjectValue(yamlObject, "rootMethodTable", true);
        if (rootMethodTableYamlObj != null) {
            rootMethodTable.fromYamlObject(rootMethodTableYamlObj);
        }

        subMethodTable = new TestMethodTable();
        Map<String, Object> subMethodTableYamlObj
        = YamlUtils.getYamlObjectValue(yamlObject, "subMethodTable", true);
        if (subMethodTableYamlObj != null) {
            subMethodTable.fromYamlObject(subMethodTableYamlObj);
        }

        rootClassTable = new TestClassTable();
        Map<String, Object> rootClassTableYamlObj
        = YamlUtils.getYamlObjectValue(yamlObject, "rootClassTable", true);
        if (rootClassTableYamlObj != null) {
            rootClassTable.fromYamlObject(rootClassTableYamlObj);
        }

        subClassTable = new TestClassTable();
        Map<String, Object> subClassTableYamlObj
        = YamlUtils.getYamlObjectValue(yamlObject, "subClassTable", true);
        if (subClassTableYamlObj != null) {
            subClassTable.fromYamlObject(subClassTableYamlObj);
        }
    }

    public TestClass getTestClassByKey(String testClassKey) throws IllegalDataStructureException {
        TestClass subClass = subClassTable.getByKey(testClassKey);
        if (subClass != null) {
            return subClass;
        }
        TestClass rootClass = rootClassTable.getByKey(testClassKey);
        if (rootClass != null) {
            return rootClass;
        }
        throw new IllegalDataStructureException(String.format(MSG_CLASS_NOT_FOUND, testClassKey));
    }

    public TestMethod getTestMethodByKey(String testMethodKey)
            throws IllegalDataStructureException {
        TestMethod subMethod = subMethodTable.getByKey(testMethodKey);
        if (subMethod != null) {
            return subMethod;
        }
        TestMethod rootMethod = rootMethodTable.getByKey(testMethodKey);
        if (rootMethod != null) {
            return rootMethod;
        }
        throw new IllegalDataStructureException(String.format(MSG_METHOD_NOT_FOUND, testMethodKey));
    }

    public TestField getTestFieldByKey(String testFieldKey)
            throws IllegalDataStructureException {
        TestField subField = subFieldTable.getByKey(testFieldKey);
        if (subField != null) {
            return subField;
        }
        TestField rootField = rootFieldTable.getByKey(testFieldKey);
        if (rootField != null) {
            return rootField;
        }
        throw new IllegalDataStructureException(String.format(MSG_FIELD_NOT_FOUND, testFieldKey));
    }

    private void resolveTestClass(TestMethod testMethod) throws IllegalDataStructureException {
        testMethod.setTestClass(getTestClassByKey(testMethod.getTestClassKey()));
    }

    private void resolveTestMethod(TestClass testClass) throws IllegalDataStructureException {
        testClass.clearTestMethods();
        for (String testMethodKey : testClass.getTestMethodKeys()) {
            TestMethod testMethod = getTestMethodByKey(testMethodKey);
            testClass.addTestMethod(testMethod);
        }
    }

    private void resolveTestField(TestClass testClass) throws IllegalDataStructureException {
        testClass.clearTestFields();
        for (String testFieldKey : testClass.getTestFieldKeys()) {
            TestField testField = getTestFieldByKey(testFieldKey);
            testClass.addTestField(testField);
        }
    }

    private void resolveDelegateToTestClass(TestClass testClass)
            throws IllegalDataStructureException {
        if (testClass.getDelegateToTestClassKey() == null) {
            testClass.setDelegateToTestClass(null);
        } else {
            testClass.setDelegateToTestClass(
                    getTestClassByKey(testClass.getDelegateToTestClassKey()));
        }
    }

    private void resolveTestMethod(Code code) throws IllegalDataStructureException {
        if (code instanceof SubMethodInvoke) {
            SubMethodInvoke invoke = (SubMethodInvoke) code;
            TestMethod testMethod = getTestMethodByKey(invoke.getSubMethodKey());
            invoke.setSubMethod(testMethod);
            resolveTestMethod(invoke.getThisInstance());
            for (Code arg : invoke.getArgs()) {
                resolveTestMethod(arg);
            }
        } else if (code instanceof LocalVarAssign) {
            LocalVarAssign assign = (LocalVarAssign) code;
            resolveTestMethod(assign.getValue());
        } else if (code instanceof TestStep) {
            TestStep testStep = (TestStep) code;
            for (CodeLine step : testStep.getStepBody()) {
                resolveTestMethod(step.getCode());
            }
        }
    }

    // resolve all methodKey and classKey references.
    // assume all keys have been set
    public void resolveKeyReference() throws IllegalDataStructureException {
        for (TestClass testClass : rootClassTable.getTestClasses()) {
            resolveTestMethod(testClass);
            resolveTestField(testClass);
            resolveDelegateToTestClass(testClass);
        }
        for (TestClass testClass : subClassTable.getTestClasses()) {
            resolveTestMethod(testClass);
            resolveTestField(testClass);
            resolveDelegateToTestClass(testClass);
        }
        for (TestMethod testMethod : rootMethodTable.getTestMethods()) {
            resolveTestClass(testMethod);
            for (CodeLine codeLine : testMethod.getCodeBody()) {
                resolveTestMethod(codeLine.getCode());
            }
        }
        for (TestMethod testMethod : subMethodTable.getTestMethods()) {
            resolveTestClass(testMethod);
            for (CodeLine codeLine : testMethod.getCodeBody()) {
                resolveTestMethod(codeLine.getCode());
            }
        }
    }

}
