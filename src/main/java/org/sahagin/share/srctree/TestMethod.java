package org.sahagin.share.srctree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.sahagin.runlib.external.CaptureStyle;
import org.sahagin.share.srctree.code.CodeLine;
import org.sahagin.share.yaml.YamlUtils;
import org.sahagin.share.yaml.YamlConvertException;
import org.sahagin.share.yaml.YamlConvertible;

public class TestMethod implements YamlConvertible {
    private String testClassKey;
    private TestClass testClass;
    private String key;
    private String simpleName;
    private String testDoc;
    private CaptureStyle captureStyle = CaptureStyle.getDefault();
    private List<String> argVariables = new ArrayList<String>(4);
    private int variableLengthArgIndex = -1;
    private List<CodeLine> codeBody = new ArrayList<CodeLine>(32);

    public String getTestClassKey() {
        return testClassKey;
    }

    public void setTestClassKey(String testClassKey) {
        this.testClassKey = testClassKey;
    }

    public TestClass getTestClass() {
        return testClass;
    }

    public void setTestClass(TestClass testClass) {
        this.testClass = testClass;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private static String argClassQualifiedNamesToArgClassesStr(
            List<String> argClassQualifiedNames) {
        if (argClassQualifiedNames.size() == 0) {
            return "void";
        } else {
            String result = "";
            for (int i = 0; i < argClassQualifiedNames.size(); i++) {
                String argClassQualifiedName = argClassQualifiedNames.get(i);
                if (i != 0) {
                    result = result + ",";
                }
                if (StringUtils.equals(argClassQualifiedName, String.class.getCanonicalName())) {
                    result = result + String.class.getSimpleName();
                } else if (StringUtils.equals(argClassQualifiedName, String[].class.getCanonicalName())) {
                    result = result + String[].class.getSimpleName();
                } else if (StringUtils.equals(argClassQualifiedName, String[][].class.getCanonicalName())) {
                    result = result + String[][].class.getSimpleName();
                } else if (StringUtils.equals(argClassQualifiedName, Object.class.getCanonicalName())) {
                    result = result + Object.class.getSimpleName();
                } else if (StringUtils.equals(argClassQualifiedName, Object[].class.getCanonicalName())) {
                    result = result + Object[].class.getSimpleName();
                } else if (StringUtils.equals(argClassQualifiedName, Object[][].class.getCanonicalName())) {
                    result = result + Object[][].class.getSimpleName();
                } else {
                    result = result + argClassQualifiedName;
                }
            }
            return result;
        }
    }

    // null argClassesStr means no information,
    private static String generateMethodKeySub(String classQualifiedName,
            String methodSimpleName, String argClassesStr) {
        if (methodSimpleName == null) {
            throw new NullPointerException();
        }

        String result;
        if (classQualifiedName != null && !classQualifiedName.equals("")) {
            result = classQualifiedName + "." + methodSimpleName;
        } else {
            result = methodSimpleName;
        }
        if (argClassesStr == null) {
            return result;
        } else {
            return result + "-" + argClassesStr;
        }
    }

    // TODO consider more about no argClassesStr method key
    // (this is generated by additional testDoc)
    public static String generateMethodKey(String classQualifiedName, String methodSimpleName) {
        return generateMethodKeySub(classQualifiedName, methodSimpleName, null);
    }

    public static String generateMethodKey(String classQualifiedName,
            String methodSimpleName, List<String> argClassQualifiedNames) {
        if (argClassQualifiedNames == null) {
            throw new NullPointerException();
        }
        String argClassesStr = argClassQualifiedNamesToArgClassesStr(argClassQualifiedNames);
        return generateMethodKeySub(classQualifiedName, methodSimpleName, argClassesStr);
    }

    public static String generateMethodKey(String classQualifiedName,
            String methodSimpleName, String argClassesStr) {
        if (argClassesStr == null) {
            throw new NullPointerException();
        }
        return generateMethodKeySub(classQualifiedName, methodSimpleName, argClassesStr);
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getQualifiedName() {
        if (testClass == null || simpleName == null) {
            return simpleName;
        } else {
            return testClass.getQualifiedName() + "." + simpleName;
        }
    }

    public String getTestDoc() {
        return testDoc;
    }

    public void setTestDoc(String testDoc) {
        this.testDoc = testDoc;
    }

    public CaptureStyle getCaptureStyle() {
        return captureStyle;
    }

    public void setCaptureStyle(CaptureStyle captureStyle) {
        if (captureStyle == CaptureStyle.STEP_IN_ONLY) {
            throw new RuntimeException("not supported yet: " + captureStyle);
        }
        this.captureStyle = captureStyle;
    }

    public List<String> getArgVariables() {
        return argVariables;
    }

    public void addArgVariable(String argVariable) {
        argVariables.add(argVariable);
    }

    public boolean hasVariableLengthArg() {
        return variableLengthArgIndex != -1;
    }

    public int getVariableLengthArgIndex() {
        return variableLengthArgIndex;
    }

    public void setVariableLengthArgIndex(int variableLengthArgIndex) {
        this.variableLengthArgIndex = variableLengthArgIndex;
    }

    public List<CodeLine> getCodeBody() {
        return codeBody;
    }

    public void addCodeBody(CodeLine codeLine) {
        codeBody.add(codeLine);
    }

    public void addAllCodeBodies(List<CodeLine> codeLines) {
        codeBody.addAll(codeLines);
    }

    @Override
    public Map<String, Object> toYamlObject() {
        Map<String, Object> result = new HashMap<String, Object>(8);
        result.put("classKey", testClassKey);
        result.put("key", key);
        result.put("name", simpleName);
        if (testDoc != null) {
            result.put("testDoc", testDoc);
        }
        if (captureStyle != CaptureStyle.getDefault()) {
            result.put("capture", captureStyle.getValue());
        }
        if (!argVariables.isEmpty()) {
            result.put("argVariables", argVariables);
        }
        if (variableLengthArgIndex != -1) {
            result.put("varLengthArgIndex", variableLengthArgIndex);
        }
        if (!codeBody.isEmpty()) {
            result.put("codeBody", YamlUtils.toYamlObjectList(codeBody));
        }
        return result;
    }

    @Override
    public void fromYamlObject(Map<String, Object> yamlObject)
            throws YamlConvertException {
        testClass = null;
        testClassKey = YamlUtils.getStrValue(yamlObject, "classKey");
        key = YamlUtils.getStrValue(yamlObject, "key");
        simpleName = YamlUtils.getStrValue(yamlObject, "name");
        testDoc = YamlUtils.getStrValue(yamlObject, "testDoc", true);
        // captureStyle is not mandatory
        captureStyle = YamlUtils.getCaptureStyleValue(yamlObject, "capture", true);
        if (captureStyle == null) {
            captureStyle = CaptureStyle.getDefault();
        }
        argVariables = YamlUtils.getStrListValue(yamlObject, "argVariables", true);
        Integer variableLengthArgIndexObj = YamlUtils.getIntValue(yamlObject, "varLengthArgIndex", true);
        if (variableLengthArgIndexObj == null) {
            variableLengthArgIndex = -1;
        } else {
            variableLengthArgIndex = variableLengthArgIndexObj;
        }
        List<Map<String, Object>> codeBodyYamlObj
        = YamlUtils.getYamlObjectListValue(yamlObject, "codeBody", true);
        codeBody = new ArrayList<CodeLine>(codeBodyYamlObj.size());
        for (Map<String, Object> codeLineYamlObj : codeBodyYamlObj) {
            CodeLine codeLine = new CodeLine();
            codeLine.fromYamlObject(codeLineYamlObj);
            codeBody.add(codeLine);
        }
    }

}
