/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.slim3.gen.generator;

import org.slim3.gen.ClassConstants;
import org.slim3.gen.ProductInfo;
import org.slim3.gen.datastore.*;
import org.slim3.gen.desc.AttributeMetaDesc;
import org.slim3.gen.desc.ModelMetaDesc;
import org.slim3.gen.printer.Printer;

import java.lang.Boolean;
import java.lang.String;
import java.util.*;
import java.util.Date;

import static org.slim3.gen.ClassConstants.*;
import static org.slim3.gen.ClassConstants.Double;
import static org.slim3.gen.ClassConstants.Float;
import static org.slim3.gen.ClassConstants.Integer;
import static org.slim3.gen.ClassConstants.Long;
import static org.slim3.gen.ClassConstants.Object;
import static org.slim3.gen.ClassConstants.Short;
import static org.slim3.gen.ClassConstants.String;

/**
 * Generates a model meta java file.
 * 
 * @author taedium
 * @author oyama
 * @since 1.0.0
 * 
 */
public class ModelMetaGenerator implements Generator {

    /** the model meta description */
    protected final ModelMetaDesc modelMetaDesc;

    /**
     * Creates a new {@link ModelMetaGenerator}.
     * 
     * @param modelMetaDesc
     *            the model meta description
     */
    public ModelMetaGenerator(ModelMetaDesc modelMetaDesc) {
        if (modelMetaDesc == null) {
            throw new NullPointerException(
                "The modelMetaDesc parameter is null.");
        }
        this.modelMetaDesc = modelMetaDesc;
    }

    public void generate(Printer printer) {
        if (printer == null) {
            throw new NullPointerException("The printer parameter is null.");
        }
        printPackage(printer);
        printClass(printer);
    }

    /**
     * Prints the package.
     * 
     * @param printer
     *            the printer
     */
    protected void printPackage(Printer printer) {
        if (modelMetaDesc.getPackageName().length() != 0) {
            printer.println("package %s;", modelMetaDesc.getPackageName());
            printer.println();
        }
    }

    /**
     * Prints the class.
     * 
     * @param printer
     */
    protected void printClass(Printer printer) {
        printer
            .println(
                "//@javax.annotation.Generated(value = { \"%s\", \"%s\" }, date = \"%tF %<tT\")",
                ProductInfo.getName(),
                ProductInfo.getVersion(),
                new Date());
        printer.println("/** */");
        printer.println(
            "public final class %s extends %s<%s> {",
            modelMetaDesc.getSimpleName(),
            ClassConstants.ModelMeta,
            modelMetaDesc.getModelClassName());
        printer.println();
        printer.indent();
        printModelListenerFields(printer);
        printAttributeMetaFields(printer);
        printAttributeListenerFields(printer);
        printSingletonField(printer);
        printGetMethod(printer);
        printConstructor(printer);
        printer.unindent();
        printer.println();
        printer.indent();
        printEntityToModelMethod(printer);
        printModelToEntityMethod(printer);
        printGetKeyMethod(printer);
        printSetKeyMethod(printer);
        printGetVersionMethod(printer);
        printAssignKeyToModelRefIfNecessaryMethod(printer);
        printIncrementVersionMethod(printer);
        printPrePutMethod(printer);
        printPostGetMethod(printer);
        printGetSchemaVersionName(printer);
        printGetClassHierarchyListName(printer);
        printIsCipherProperty(printer);
        printCustomExtensionMethods(printer);
        printer.unindent();
        printer.print("}");
    }

    /**
     * Generates attribute meta fields.
     * 
     * @param printer
     *            the printer
     */
    protected void printAttributeMetaFields(Printer printer) {
        AttributeMetaFieldsGenerator generator =
            new AttributeMetaFieldsGenerator(printer);
        generator.generate();
    }

    /**
     * Generates attribute listener fields.
     * 
     * @param printer
     *            the printer
     */
    protected void printAttributeListenerFields(Printer printer) {
        for (AttributeMetaDesc attr : modelMetaDesc.getAttributeMetaDescList()) {
            if (!attr.isPersistent()) {
                continue;
            }
            if (attr.getAttributeListenerClassName() != null
                && !attr.getAttributeListenerClassName().equals(
                    AttributeListener)) {
                printer
                    .println(
                        "private static final %1$s slim3_%2$sAttributeListener = new %1$s();",
                        attr.getAttributeListenerClassName(),
                        attr.getAttributeName());
                printer.println();
            }
        }
    }

    /**
     * Generates model listener fields.
     * 
     * @param printer
     *            the printer
     */
    protected void printModelListenerFields(Printer printer) {
        String modelListenerClassName =
            modelMetaDesc.getModelListenerClassName();
        if (modelListenerClassName != null
            && !modelListenerClassName.equals(ModelListener)) {
            printer.println(
                "private static final %1$s slim3_modelListener = new %1$s();",
                modelListenerClassName);
            printer.println();
        }
    }

    /**
     * Prints the singleton field.
     * 
     * @param printer
     *            the printer
     */
    protected void printSingletonField(Printer printer) {
        printer.println(
            "private static final %1$s slim3_singleton = new %1$s();",
            modelMetaDesc.getSimpleName());
        printer.println();
    }

    /**
     * Prints the constructor.
     * 
     * @param printer
     *            the printer
     */
    protected void printConstructor(Printer printer) {
        printer.println("/** */");
        printer.println("public %s() {", modelMetaDesc.getSimpleName());
        if (modelMetaDesc.getClassHierarchyList().isEmpty()) {
            printer.println(
                "    super(\"%1$s\", %2$s.class);",
                modelMetaDesc.getKind(),
                modelMetaDesc.getModelClassName());
        } else {
            printer.print(
                "    super(\"%1$s\", %2$s.class, %3$s.asList(",
                modelMetaDesc.getKind(),
                modelMetaDesc.getModelClassName(),
                Arrays.class.getName());
            for (Iterator<String> it =
                modelMetaDesc.getClassHierarchyList().iterator(); it.hasNext();) {
                printer.printWithoutIndent("\"%s\"", it.next());
                if (it.hasNext()) {
                    printer.printWithoutIndent(", ");
                }
            }
            printer.printlnWithoutIndent("));");
        }
        printer.println("}");
    }

    /**
     * Generates the {@code get} method.
     * 
     * @param printer
     *            the prnter
     */
    protected void printGetMethod(Printer printer) {
        printer.println("/**");
        printer.println(" * @return the singleton");
        printer.println(" */");
        printer.println(
            "public static %1$s get() {",
            modelMetaDesc.getSimpleName());
        printer.println("   return slim3_singleton;");
        printer.println("}");
        printer.println();
    }

    /**
     * Generates the {@code entityToModel} method.
     * 
     * @param printer
     *            the prnter
     */
    protected void printEntityToModelMethod(Printer printer) {
        EntityToModelMethodGenerator generator =
            new EntityToModelMethodGenerator(printer);
        generator.generate();
    }

    /**
     * Generates the {@code modelToEntity} method.
     * 
     * @param printer
     *            the prnter
     */
    protected void printModelToEntityMethod(Printer printer) {
        ModelToEntityMethodGenerator generator =
            new ModelToEntityMethodGenerator(printer);
        generator.generate();
    }

    /**
     * Generates the {@code getVersion} method.
     * 
     * @param printer
     *            the prnter
     */
    protected void printGetVersionMethod(final Printer printer) {
        printer.println("@Override");
        printer.println("protected long getVersion(Object model) {");
        final AttributeMetaDesc attr =
            modelMetaDesc.getVersionAttributeMetaDesc();
        if (attr == null) {
            printer
                .println(
                    "    throw new IllegalStateException(\"The version property of the model(%1$s) is not defined.\");",
                    modelMetaDesc.getModelClassName());
        } else {
            printer.println(
                "    %1$s m = (%1$s) model;",
                modelMetaDesc.getModelClassName());
            DataType dataType = attr.getDataType();
            dataType.accept(
                new SimpleDataTypeVisitor<Void, Void, RuntimeException>() {

                    @Override
                    protected Void defaultAction(DataType type, Void p)
                            throws RuntimeException {
                        printer
                            .println(
                                "    throw new IllegalStateException(\"The version property of the model(%1$s) is not defined.\");",
                                modelMetaDesc.getModelClassName());
                        return null;
                    }

                    @Override
                    public Void visitPrimitiveLongType(PrimitiveLongType type,
                            Void p) throws RuntimeException {
                        printer.println(
                            "    return m.%1$s();",
                            attr.getReadMethodName());
                        return null;
                    }

                    @Override
                    public Void visitLongType(LongType type, Void p)
                            throws RuntimeException {
                        printer
                            .println(
                                "    return m.%1$s() != null ? m.%1$s().longValue() : 0L;",
                                attr.getReadMethodName());
                        return null;
                    }

                },
                null);
        }
        printer.println("}");
        printer.println();
    }

    /**
     * Generates the {@code incrementVersion} method.
     * 
     * @param printer
     *            the prnter
     */
    protected void printIncrementVersionMethod(final Printer printer) {
        printer.println("@Override");
        printer.println("protected void incrementVersion(Object model) {");
        final AttributeMetaDesc attr =
            modelMetaDesc.getVersionAttributeMetaDesc();
        if (attr != null) {
            printer.println(
                "    %1$s m = (%1$s) model;",
                modelMetaDesc.getModelClassName());
            DataType dataType = attr.getDataType();
            dataType.accept(
                new SimpleDataTypeVisitor<Void, Void, RuntimeException>() {

                    @Override
                    protected Void defaultAction(DataType type, Void p)
                            throws RuntimeException {
                        printer
                            .println(
                                "    throw new IllegalStateException(\"The version property of the model(%1$s) is not defined.\");",
                                modelMetaDesc.getModelClassName());
                        return null;
                    }

                    @Override
                    public Void visitPrimitiveLongType(PrimitiveLongType type,
                            Void p) throws RuntimeException {
                        printer.println(
                            "    m.%1$s(m.%2$s() + 1L);",
                            attr.getWriteMethodName(),
                            attr.getReadMethodName());
                        return null;
                    }

                    @Override
                    public Void visitLongType(LongType type, Void p)
                            throws RuntimeException {
                        printer
                            .println(
                                "    long version = m.%1$s() != null ? m.%1$s().longValue() : 0L;",
                                attr.getReadMethodName());
                        printer.println(
                            "    m.%1$s(Long.valueOf(version + 1L));",
                            attr.getWriteMethodName());
                        return null;
                    }

                },
                null);
        }
        printer.println("}");
        printer.println();
    }

    /**
     * Generates the {@code getSchemaVersionName} method.
     * 
     * @param printer
     *            the prnter
     */
    protected void printGetSchemaVersionName(final Printer printer) {
        printer.println("@Override");
        printer.println("public String getSchemaVersionName() {");
        printer.println(
            "    return \"%1$s\";",
            modelMetaDesc.getSchemaVersionName());
        printer.println("}");
        printer.println();
    }

    /**
     * Generates the {@code getClassHierarchyListName} method.
     * 
     * @param printer
     *            the prnter
     */
    protected void printGetClassHierarchyListName(final Printer printer) {
        printer.println("@Override");
        printer.println("public String getClassHierarchyListName() {");
        printer.println(
            "    return \"%1$s\";",
            modelMetaDesc.getClassHierarchyListName());
        printer.println("}");
        printer.println();
    }

    /**
     * Generates the {@code isCipherProperty} method.
     * 
     * @param printer
     *            the printer
     */
    protected void printIsCipherProperty(final Printer printer) {
        printer.println("@Override");
        printer
            .println("protected boolean isCipherProperty(String propertyName) {");
        for (AttributeMetaDesc attr : modelMetaDesc.getAttributeMetaDescList()) {
            if (!attr.isPersistent()) {
                continue;
            }
            if (attr.isCipher()) {
                printer.println(
                    "    if (\"%1$s\".equals(propertyName)) return true;",
                    attr.getName());
            }
        }
        printer.println("    return false;");
        printer.println("}");
        printer.println();
    }

    /**
     * Generates the {@code getKey} method.
     * 
     * @param printer
     *            the printer
     */
    protected void printGetKeyMethod(final Printer printer) {
        printer.println("@Override");
        printer
            .println("protected com.google.appengine.api.datastore.Key getKey(Object model) {");
        final AttributeMetaDesc attr = modelMetaDesc.getKeyAttributeMetaDesc();
        if (attr == null) {
            printer
                .println(
                    "    throw new IllegalStateException(\"The key property of the model(%1$s) is not defined.\");",
                    modelMetaDesc.getModelClassName());
        } else {
            printer.println(
                "    %1$s m = (%1$s) model;",
                modelMetaDesc.getModelClassName());
            printer.println("    return m.%1$s();", attr.getReadMethodName());
        }
        printer.println("}");
        printer.println();
    }

    /**
     * Generates the {@code setKey} method.
     * 
     * @param printer
     *            the printer
     */
    protected void printSetKeyMethod(final Printer printer) {
        printer.println("@Override");
        printer
            .println("protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {");
        final AttributeMetaDesc attr = modelMetaDesc.getKeyAttributeMetaDesc();
        if (attr == null) {
            printer
                .println(
                    "    throw new IllegalStateException(\"The key property of the model(%1$s) is not defined.\");",
                    modelMetaDesc.getModelClassName());
        } else {
            printer.println("    validateKey(key);");
            printer.println(
                "    %1$s m = (%1$s) model;",
                modelMetaDesc.getModelClassName());
            printer.println("    m.%1$s(key);", attr.getWriteMethodName());
        }
        printer.println("}");
        printer.println();
    }

    /**
     * Generates the {@code setKey} method.
     * 
     * @param printer
     *            the printer
     */
    protected void printPrePutMethod(final Printer printer) {
        printer.println("@Override");
        printer.println("protected void prePut(Object model) {");

        boolean first = true;
        for (AttributeMetaDesc attr : modelMetaDesc.getAttributeMetaDescList()) {
            if (attr.getAttributeListenerClassName() != null
                && !attr.getAttributeListenerClassName().equals(
                    AttributeListener)) {
                if (!attr.isPersistent()) {
                    continue;
                }
                if (first) {
                    printer.println(
                        "    %1$s m = (%1$s) model;",
                        modelMetaDesc.getModelClassName());
                    first = false;
                }
                printer
                    .println(
                        "    m.%1$s(slim3_%2$sAttributeListener.prePut(m.%3$s()));",
                        attr.getWriteMethodName(),
                        attr.getAttributeName(),
                        attr.getReadMethodName());
            }
        }

        String modelListenerClassName =
            modelMetaDesc.getModelListenerClassName();
        if (modelListenerClassName != null
            && !modelListenerClassName.equals(ModelListener)) {
            printer.println(
                "    slim3_modelListener.prePut((%1$s) model);",
                modelMetaDesc.getModelClassName());
        }

        printer.println("}");
        printer.println();
    }

    /**
     * Generates the {@code postGet} method.
     * 
     * @param printer
     *            the printer
     */
    protected void printPostGetMethod(final Printer printer) {
        printer.println("@Override");
        printer.println("protected void postGet(Object model) {");

        String modelListenerClassName =
            modelMetaDesc.getModelListenerClassName();
        if (modelListenerClassName != null
            && !modelListenerClassName.equals(ModelListener)) {
            printer.println(
                "    slim3_modelListener.postGet((%1$s) model);",
                modelMetaDesc.getModelClassName());
        }

        printer.println("}");
        printer.println();
    }

    /**
     * Generates the {@code setKey} method.
     * 
     * @param printer
     *            the printer
     */
    protected void printAssignKeyToModelRefIfNecessaryMethod(
            final Printer printer) {
        AssignKeyToModelRefIfNecessaryMethodGenerator generator =
            new AssignKeyToModelRefIfNecessaryMethodGenerator(printer);
        generator.generate();
    }


    /**
     * Empty method body to be overwritten by custom extensions
     * 
     * @param printer
     *            the printer
     */
    protected void printCustomExtensionMethods(final Printer printer) {
    }

    /**
     * Represents attribute meta fields generator.
     * 
     * @author taedium
     * @since 1.0.0
     * 
     */
    protected class AttributeMetaFieldsGenerator extends
            SimpleDataTypeVisitor<Void, AttributeMetaDesc, RuntimeException> {

        /** the printer */
        protected final Printer printer;

        /**
         * Creates a new {@link AttributeMetaFieldsGenerator}.
         * 
         * @param printer
         *            the printer
         */
        protected AttributeMetaFieldsGenerator(Printer printer) {
            this.printer = printer;
        }

        /**
         * Generates attribute meta fields.
         */
        public void generate() {
            for (AttributeMetaDesc attr : modelMetaDesc
                .getAttributeMetaDescList()) {
                if (!attr.isPersistent()) {
                    continue;
                }
                DataType dataType = attr.getDataType();
                dataType.accept(this, attr);
            }
        }

        @Override
        protected Void defaultAction(DataType type, AttributeMetaDesc p)
                throws RuntimeException {
            if (p.isLob() || p.isUnindexed()) {
                printer.println("/** */");
                printer
                    .println(
                        "public final %1$s<%2$s, %3$s> %4$s = new %1$s<%2$s, %3$s>(this, \"%5$s\", \"%4$s\", %6$s.class);",
                        UnindexedAttributeMeta,
                        modelMetaDesc.getModelClassName(),
                        type.getTypeName(),
                        p.getAttributeName(),
                        p.getName(),
                        type.getClassName());
                printer.println();
            }
            return null;
        }

        @Override
        public Void visitCorePrimitiveType(CorePrimitiveType type,
                AttributeMetaDesc p) throws RuntimeException {
            printer.println("/** */");
            printer
                .println(
                    "public final %1$s<%2$s, %3$s> %4$s = new %1$s<%2$s, %3$s>(this, \"%5$s\", \"%4$s\", %6$s.class);",
                    CoreAttributeMeta,
                    modelMetaDesc.getModelClassName(),
                    type.getWrapperClassName(),
                    p.getAttributeName(),
                    p.getName(),
                    type.getClassName());
            printer.println();
            return null;
        }

        @Override
        public Void visitCoreReferenceType(CoreReferenceType type,
                AttributeMetaDesc p) throws RuntimeException {
            printer.println("/** */");
            if (p.isLob() || p.isUnindexed()) {
                printer
                    .println(
                        "public final %1$s<%2$s, %3$s> %4$s = new %1$s<%2$s, %3$s>(this, \"%5$s\", \"%4$s\", %6$s.class);",
                        CoreUnindexedAttributeMeta,
                        modelMetaDesc.getModelClassName(),
                        type.getTypeName(),
                        p.getAttributeName(),
                        p.getName(),
                        type.getClassName());
            } else {
                printer
                    .println(
                        "public final %1$s<%2$s, %3$s> %4$s = new %1$s<%2$s, %3$s>(this, \"%5$s\", \"%4$s\", %6$s.class);",
                        CoreAttributeMeta,
                        modelMetaDesc.getModelClassName(),
                        type.getTypeName(),
                        p.getAttributeName(),
                        p.getName(),
                        type.getClassName());
            }
            printer.println();
            return null;
        }

        @Override
        public Void visitModelRefType(ModelRefType type, AttributeMetaDesc p)
                throws RuntimeException {
            printer.println("/** */");
            printer
                .println(
                    "public final %1$s<%2$s, %3$s, %4$s> %5$s = new %1$s<%2$s, %3$s, %4$s>(this, \"%6$s\", \"%5$s\", %7$s.class, %8$s.class);",
                    ModelRefAttributeMeta,
                    modelMetaDesc.getModelClassName(),
                    type.getTypeName(),
                    type.getReferenceModelTypeName(),
                    p.getAttributeName(),
                    p.getName(),
                    type.getClassName(),
                    type.getReferenceModelTypeName());
            printer.println();
            return null;
        }

        @Override
        public Void visitKeyType(KeyType type, AttributeMetaDesc p)
                throws RuntimeException {
            printer.println("/** */");
            printer
                .println(
                    "public final %1$s<%2$s, %3$s> %4$s = new %1$s<%2$s, %3$s>(this, \"%5$s\", \"%4$s\", %6$s.class);",
                    CoreAttributeMeta,
                    modelMetaDesc.getModelClassName(),
                    type.getTypeName(),
                    p.getAttributeName(),
                    p.getName(),
                    type.getClassName());
            printer.println();
            return null;
        }

        @Override
        public Void visitStringType(StringType type, AttributeMetaDesc p)
                throws RuntimeException {
            printer.println("/** */");
            if (p.isLob() || p.isUnindexed()) {
                printer
                    .println(
                        "public final %1$s<%2$s> %3$s = new %1$s<%2$s>(this, \"%4$s\", \"%3$s\");",
                        StringUnindexedAttributeMeta,
                        modelMetaDesc.getModelClassName(),
                        p.getAttributeName(),
                        p.getName(),
                        type.getClassName());
            } else {
                printer
                    .println(
                        "public final %1$s<%2$s> %3$s = new %1$s<%2$s>(this, \"%4$s\", \"%3$s\");",
                        StringAttributeMeta,
                        modelMetaDesc.getModelClassName(),
                        p.getAttributeName(),
                        p.getName());
            }
            printer.println();
            return null;
        }

        @Override
        public Void visitBlobType(BlobType type, AttributeMetaDesc p)
                throws RuntimeException {
            printer.println("/** */");
            printer
                .println(
                    "public final %1$s<%2$s, %3$s> %4$s = new %1$s<%2$s, %3$s>(this, \"%5$s\", \"%4$s\", %6$s.class);",
                    UnindexedAttributeMeta,
                    modelMetaDesc.getModelClassName(),
                    type.getClassName(),
                    p.getAttributeName(),
                    p.getName(),
                    type.getClassName());
            printer.println();
            return null;
        }

        @Override
        public Void visitTextType(TextType type, AttributeMetaDesc p)
                throws RuntimeException {
            printer.println("/** */");
            printer
                .println(
                    "public final %1$s<%2$s, %3$s> %4$s = new %1$s<%2$s, %3$s>(this, \"%5$s\", \"%4$s\", %6$s.class);",
                    UnindexedAttributeMeta,
                    modelMetaDesc.getModelClassName(),
                    type.getClassName(),
                    p.getAttributeName(),
                    p.getName(),
                    type.getClassName());
            printer.println();
            return null;
        }

        @Override
        public Void visitCollectionType(final CollectionType collectionType,
                final AttributeMetaDesc attr) throws RuntimeException {
            DataType elementType = collectionType.getElementType();
            elementType.accept(
                new SimpleDataTypeVisitor<Void, Void, RuntimeException>() {

                    @Override
                    public Void visitStringType(StringType type, Void p)
                            throws RuntimeException {
                        printer.println("/** */");
                        if (attr.isLob() || attr.isUnindexed()) {
                            printer
                                .println(
                                    "public final %1$s<%2$s, %3$s> %4$s = new %1$s<%2$s, %3$s>(this, \"%5$s\", \"%4$s\", %6$s.class);",
                                    StringCollectionUnindexedAttributeMeta,
                                    modelMetaDesc.getModelClassName(),
                                    collectionType.getTypeName(),
                                    attr.getAttributeName(),
                                    attr.getName(),
                                    collectionType.getClassName());
                        } else {
                            printer
                                .println(
                                    "public final %1$s<%2$s, %3$s> %4$s = new %1$s<%2$s, %3$s>(this, \"%5$s\", \"%4$s\", %6$s.class);",
                                    StringCollectionAttributeMeta,
                                    modelMetaDesc.getModelClassName(),
                                    collectionType.getTypeName(),
                                    attr.getAttributeName(),
                                    attr.getName(),
                                    collectionType.getClassName());
                        }
                        printer.println();
                        return null;
                    }

                    @Override
                    public Void visitCoreReferenceType(
                            CoreReferenceType elementType, Void p)
                            throws RuntimeException {
                        printer.println("/** */");
                        if (attr.isLob() || attr.isUnindexed()) {
                            printer
                                .println(
                                    "public final %1$s<%2$s, %3$s, %4$s> %5$s = new %1$s<%2$s, %3$s, %4$s>(this, \"%6$s\", \"%5$s\", %7$s.class);",
                                    CollectionUnindexedAttributeMeta,
                                    modelMetaDesc.getModelClassName(),
                                    collectionType.getTypeName(),
                                    elementType.getTypeName(),
                                    attr.getAttributeName(),
                                    attr.getName(),
                                    collectionType.getClassName());
                        } else {
                            printer
                                .println(
                                    "public final %1$s<%2$s, %3$s, %4$s> %5$s = new %1$s<%2$s, %3$s, %4$s>(this, \"%6$s\", \"%5$s\", %7$s.class);",
                                    CollectionAttributeMeta,
                                    modelMetaDesc.getModelClassName(),
                                    collectionType.getTypeName(),
                                    elementType.getTypeName(),
                                    attr.getAttributeName(),
                                    attr.getName(),
                                    collectionType.getClassName());
                        }
                        printer.println();
                        return null;
                    }

                },
                null);
            return null;
        }

        @Override
        public Void visitArrayType(ArrayType type, AttributeMetaDesc p)
                throws RuntimeException {
            printer.println("/** */");
            if (p.isLob() || p.isUnindexed()) {
                printer
                    .println(
                        "public final %1$s<%2$s, %3$s> %4$s = new %1$s<%2$s, %3$s>(this, \"%5$s\", \"%4$s\", %6$s.class);",
                        CoreUnindexedAttributeMeta,
                        modelMetaDesc.getModelClassName(),
                        type.getTypeName(),
                        p.getAttributeName(),
                        p.getName(),
                        type.getClassName());
            } else {
                printer
                    .println(
                        "public final %1$s<%2$s, %3$s> %4$s = new %1$s<%2$s, %3$s>(this, \"%5$s\", \"%4$s\", %6$s.class);",
                        CoreAttributeMeta,
                        modelMetaDesc.getModelClassName(),
                        type.getTypeName(),
                        p.getAttributeName(),
                        p.getName(),
                        type.getClassName());
            }
            printer.println();
            return null;
        }
    }

    /**
     * Represents the {@code entityToModel} method generator.
     * 
     * @author taedium
     * @since 1.0.0
     * 
     */
    protected class EntityToModelMethodGenerator extends
            SimpleDataTypeVisitor<Void, AttributeMetaDesc, RuntimeException> {

        /** the printer */
        protected final Printer printer;

        /**
         * Creates a new {@link EntityToModelMethodGenerator}.
         * 
         * @param printer
         *            the printer
         */
        public EntityToModelMethodGenerator(Printer printer) {
            this.printer = printer;
        }

        /**
         * Generates the entityToModelMethod.
         */
        public void generate() {
            printer.println("@Override");
            printer.println(
                "public %1$s entityToModel(%2$s entity) {",
                modelMetaDesc.getModelClassName(),
                Entity);
            printer.indent();
            if (modelMetaDesc.isAbstrct()) {
                printer.println(
                    "throw new %1$s(\"The class(%2$s) is abstract.\");",
                    UnsupportedOperationException.class.getName(),
                    modelMetaDesc.getModelClassName());
            } else {
                printer.println(
                    "%1$s model = new %1$s();",
                    modelMetaDesc.getModelClassName());
                for (AttributeMetaDesc attr : modelMetaDesc
                    .getAttributeMetaDescList()) {
                    if (!attr.isPersistent()) {
                        continue;
                    }
                    DataType dataType = attr.getDataType();
                    dataType.accept(this, attr);
                }
                printer.println("return model;");
            }
            printer.unindent();
            printer.println("}");
            printer.println();
        }

        @Override
        protected Void defaultAction(DataType type, AttributeMetaDesc p)
                throws RuntimeException {
            printer
                .println(
                    "%1$s _%2$s = blobToSerializable((%3$s) entity.getProperty(\"%4$s\"));",
                    type.getTypeName(),
                    p.getAttributeName(),
                    Blob,
                    p.getName());
            printer.println(
                "model.%1$s(_%2$s);",
                p.getWriteMethodName(),
                p.getAttributeName());
            return null;
        }

        @Override
        public Void visitPrimitiveBooleanType(PrimitiveBooleanType type,
                AttributeMetaDesc p) throws RuntimeException {
            printer
                .println(
                    "model.%1$s(booleanToPrimitiveBoolean((%2$s) entity.getProperty(\"%3$s\")));",
                    p.getWriteMethodName(),
                    type.getWrapperClassName(),
                    p.getName());
            return null;
        }

        @Override
        public Void visitPrimitiveDoubleType(PrimitiveDoubleType type,
                AttributeMetaDesc p) throws RuntimeException {
            printer
                .println(
                    "model.%1$s(doubleToPrimitiveDouble((%2$s) entity.getProperty(\"%3$s\")));",
                    p.getWriteMethodName(),
                    type.getWrapperClassName(),
                    p.getName());
            return null;
        }

        @Override
        public Void visitPrimitiveFloatType(PrimitiveFloatType type,
                AttributeMetaDesc p) throws RuntimeException {
            printer
                .println(
                    "model.%1$s(doubleToPrimitiveFloat((%2$s) entity.getProperty(\"%3$s\")));",
                    p.getWriteMethodName(),
                    Double,
                    p.getName());
            return null;
        }

        @Override
        public Void visitPrimitiveIntType(PrimitiveIntType type,
                AttributeMetaDesc p) throws RuntimeException {
            printer
                .println(
                    "model.%1$s(longToPrimitiveInt((%2$s) entity.getProperty(\"%3$s\")));",
                    p.getWriteMethodName(),
                    Long,
                    p.getName());
            return null;
        }

        @Override
        public Void visitPrimitiveLongType(PrimitiveLongType type,
                AttributeMetaDesc p) throws RuntimeException {
            printer
                .println(
                    "model.%1$s(longToPrimitiveLong((%2$s) entity.getProperty(\"%3$s\")));",
                    p.getWriteMethodName(),
                    type.getWrapperClassName(),
                    p.getName());
            return null;
        }

        @Override
        public Void visitPrimitiveShortType(PrimitiveShortType type,
                AttributeMetaDesc p) throws RuntimeException {
            printer
                .println(
                    "model.%1$s(longToPrimitiveShort((%2$s) entity.getProperty(\"%3$s\")));",
                    p.getWriteMethodName(),
                    Long,
                    p.getName());
            return null;
        }

        @Override
        public Void visitCoreReferenceType(CoreReferenceType type,
                AttributeMetaDesc p) throws RuntimeException {
            printer.println(
                "model.%1$s((%2$s) entity.getProperty(\"%3$s\"));",
                p.getWriteMethodName(),
                type.getTypeName(),
                p.getName());
            return null;
        }

        @Override
        public Void visitFloatType(FloatType type, AttributeMetaDesc p)
                throws RuntimeException {
            printer
                .println(
                    "model.%1$s(doubleToFloat((%2$s) entity.getProperty(\"%3$s\")));",
                    p.getWriteMethodName(),
                    Double,
                    p.getName());
            return null;
        }

        @Override
        public Void visitIntegerType(IntegerType type, AttributeMetaDesc p)
                throws RuntimeException {
            printer
                .println(
                    "model.%1$s(longToInteger((%2$s) entity.getProperty(\"%3$s\")));",
                    p.getWriteMethodName(),
                    Long,
                    p.getName());
            return null;
        }

        @Override
        public Void visitShortType(ShortType type, AttributeMetaDesc p)
                throws RuntimeException {
            printer
                .println(
                    "model.%1$s(longToShort((%2$s) entity.getProperty(\"%3$s\")));",
                    p.getWriteMethodName(),
                    Long,
                    p.getName());
            return null;
        }

        @Override
        public Void visitStringType(StringType type, AttributeMetaDesc p)
                throws RuntimeException {
            if (p.isLob() && p.isCipher()) {
                printer
                    .println(
                        "model.%1$s(decrypt(textToString((%2$s) entity.getProperty(\"%3$s\"))));",
                        p.getWriteMethodName(),
                        Text,
                        p.getName());
                return null;
            }
            if (p.isLob()) {
                printer
                    .println(
                        "model.%1$s(textToString((%2$s) entity.getProperty(\"%3$s\")));",
                        p.getWriteMethodName(),
                        Text,
                        p.getName());
                return null;
            }
            if (p.isCipher()) {
                printer.println(
                    "model.%1$s(decrypt((%2$s)entity.getProperty(\"%3$s\")));",
                    p.getWriteMethodName(),
                    type.getTypeName(),
                    p.getName());
                return null;
            }
            return super.visitStringType(type, p);
        }

        @Override
        public Void visitEnumType(EnumType type, AttributeMetaDesc p)
                throws RuntimeException {
            printer
                .println(
                    "model.%1$s(stringToEnum(%2$s.class, (%3$s) entity.getProperty(\"%4$s\")));",
                    p.getWriteMethodName(),
                    type.getTypeName(),
                    String,
                    p.getName());
            return null;
        }

        @Override
        public Void visitTextType(TextType type, AttributeMetaDesc p)
                throws RuntimeException {
            if (p.isCipher()) {
                printer
                    .println(
                        "model.%1$s(decrypt((%2$s) entity.getProperty(\"%3$s\")));",
                        p.getWriteMethodName(),
                        type.getTypeName(),
                        p.getName());
                return null;
            }
            return super.visitTextType(type, p);
        }

        @Override
        public Void visitKeyType(KeyType type, AttributeMetaDesc p)
                throws RuntimeException {
            if (p.isPrimaryKey()) {
                printer.println(
                    "model.%1$s(entity.getKey());",
                    p.getWriteMethodName());
            } else {
                printer.println(
                    "model.%1$s((%2$s) entity.getProperty(\"%3$s\"));",
                    p.getWriteMethodName(),
                    type.getTypeName(),
                    p.getName());
            }
            return null;
        }

        @Override
        public Void visitModelRefType(ModelRefType type, AttributeMetaDesc p)
                throws RuntimeException {
            printer.println(
                "if (model.%1$s() == null) {",
                p.getReadMethodName());
            printer
                .println(
                    "    throw new NullPointerException(\"The property(%1$s) is null.\");",
                    p.getAttributeName());
            printer.println("}");
            printer.println(
                "model.%1$s().setKey((%2$s) entity.getProperty(\"%3$s\"));",
                p.getReadMethodName(),
                Key,
                p.getName());
            return null;
        }

        @Override
        public Void visitArrayType(ArrayType type, final AttributeMetaDesc attr)
                throws RuntimeException {
            DataType componentType = type.getComponentType();
            boolean accepted =
                componentType.accept(
                    new SimpleDataTypeVisitor<Boolean, Void, RuntimeException>(
                        false) {

                        @Override
                        public Boolean visitPrimitiveByteType(
                                PrimitiveByteType type, Void p)
                                throws RuntimeException {
                            if (attr.isLob()) {
                                printer
                                    .println(
                                        "model.%1$s(blobToBytes((%2$s) entity.getProperty(\"%3$s\")));",
                                        attr.getWriteMethodName(),
                                        Blob,
                                        attr.getName());
                            } else {
                                printer
                                    .println(
                                        "model.%1$s(shortBlobToBytes((%2$s) entity.getProperty(\"%3$s\")));",
                                        attr.getWriteMethodName(),
                                        ShortBlob,
                                        attr.getName());
                            }
                            return true;
                        }

                    },
                    null);
            if (accepted) {
                return null;
            }
            return super.visitArrayType(type, attr);
        }

        @Override
        public Void visitListType(final ListType collectionType,
                final AttributeMetaDesc attr) throws RuntimeException {
            DataType elementType = collectionType.getElementType();
            Boolean handled =
                elementType.accept(
                    new SimpleDataTypeVisitor<Boolean, Void, RuntimeException>(
                        false) {

                        @Override
                        public Boolean visitCoreReferenceType(
                                CoreReferenceType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(toList(%2$s.class, entity.getProperty(\"%3$s\")));",
                                    attr.getWriteMethodName(),
                                    type.getClassName(),
                                    attr.getName());
                            return true;
                        }

                        @Override
                        public Boolean visitShortType(ShortType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(longListToShortList(entity.getProperty(\"%2$s\")));",
                                    attr.getWriteMethodName(),
                                    attr.getName());
                            return true;
                        }

                        @Override
                        public Boolean visitIntegerType(IntegerType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(longListToIntegerList(entity.getProperty(\"%2$s\")));",
                                    attr.getWriteMethodName(),
                                    attr.getName());
                            return true;
                        }

                        @Override
                        public Boolean visitFloatType(FloatType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(doubleListToFloatList(entity.getProperty(\"%2$s\")));",
                                    attr.getWriteMethodName(),
                                    attr.getName());
                            return true;
                        }

                        @Override
                        public Boolean visitEnumType(EnumType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(stringListToEnumList(%2$s.class, entity.getProperty(\"%3$s\")));",
                                    attr.getWriteMethodName(),
                                    type.getTypeName(),
                                    attr.getName());
                            return true;
                        }
                    },
                    null);
            return handled ? null : super.visitListType(collectionType, attr);
        }

        @Override
        public Void visitLinkedListType(final LinkedListType collectionType,
                final AttributeMetaDesc attr) throws RuntimeException {
            DataType elementType = collectionType.getElementType();
            Boolean handled =
                elementType.accept(
                    new SimpleDataTypeVisitor<Boolean, Void, RuntimeException>(
                        false) {

                        @Override
                        public Boolean visitCoreReferenceType(
                                CoreReferenceType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %4$s<%2$s>(toList(%2$s.class, entity.getProperty(\"%3$s\"))));",
                                    attr.getWriteMethodName(),
                                    type.getClassName(),
                                    attr.getName(),
                                    LinkedList);
                            return true;
                        }

                        @Override
                        public Boolean visitShortType(ShortType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %3$s<%4$s>(longListToShortList(entity.getProperty(\"%2$s\"))));",
                                    attr.getWriteMethodName(),
                                    attr.getName(),
                                    LinkedList,
                                    Short);
                            return true;
                        }

                        @Override
                        public Boolean visitIntegerType(IntegerType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %3$s<%4$s>(longListToIntegerList(entity.getProperty(\"%2$s\"))));",
                                    attr.getWriteMethodName(),
                                    attr.getName(),
                                    LinkedList,
                                    Integer);
                            return true;
                        }

                        @Override
                        public Boolean visitFloatType(FloatType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %3$s<%4$s>(doubleListToFloatList(entity.getProperty(\"%2$s\"))));",
                                    attr.getWriteMethodName(),
                                    attr.getName(),
                                    LinkedList,
                                    Float);
                            return true;
                        }

                        @Override
                        public Boolean visitEnumType(EnumType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %4$s<%2$s>(stringListToEnumList(%2$s.class, entity.getProperty(\"%3$s\"))));",
                                    attr.getWriteMethodName(),
                                    type.getTypeName(),
                                    attr.getName(),
                                    LinkedList);
                            return true;
                        }
                    },
                    null);
            return handled ? null : super.visitListType(collectionType, attr);
        }

        @Override
        public Void visitSetType(final SetType collectionType,
                final AttributeMetaDesc attr) throws RuntimeException {
            DataType elementType = collectionType.getElementType();
            Boolean handled =
                elementType.accept(
                    new SimpleDataTypeVisitor<Boolean, Void, RuntimeException>(
                        false) {

                        @Override
                        public Boolean visitCoreReferenceType(
                                CoreReferenceType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %4$s<%2$s>(toList(%2$s.class, entity.getProperty(\"%3$s\"))));",
                                    attr.getWriteMethodName(),
                                    type.getClassName(),
                                    attr.getName(),
                                    HashSet);
                            return true;
                        }

                        @Override
                        public Boolean visitShortType(ShortType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %3$s<%4$s>(longListToShortList(entity.getProperty(\"%2$s\"))));",
                                    attr.getWriteMethodName(),
                                    attr.getName(),
                                    HashSet,
                                    Short);
                            return true;
                        }

                        @Override
                        public Boolean visitIntegerType(IntegerType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %3$s<%4$s>(longListToIntegerList(entity.getProperty(\"%2$s\"))));",
                                    attr.getWriteMethodName(),
                                    attr.getName(),
                                    HashSet,
                                    Integer);
                            return true;
                        }

                        @Override
                        public Boolean visitFloatType(FloatType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %3$s<%4$s>(doubleListToFloatList(entity.getProperty(\"%2$s\"))));",
                                    attr.getWriteMethodName(),
                                    attr.getName(),
                                    HashSet,
                                    Float);
                            return true;
                        }

                        @Override
                        public Boolean visitEnumType(EnumType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %4$s<%2$s>(stringListToEnumList(%2$s.class, entity.getProperty(\"%3$s\"))));",
                                    attr.getWriteMethodName(),
                                    type.getTypeName(),
                                    attr.getName(),
                                    HashSet);
                            return true;
                        }
                    },
                    null);
            return handled ? null : super.visitSetType(collectionType, attr);
        }

        @Override
        public Void visitLinkedHashSetType(
                final LinkedHashSetType collectionType,
                final AttributeMetaDesc attr) throws RuntimeException {
            DataType elementType = collectionType.getElementType();
            Boolean handled =
                elementType.accept(
                    new SimpleDataTypeVisitor<Boolean, Void, RuntimeException>(
                        false) {

                        @Override
                        public Boolean visitCoreReferenceType(
                                CoreReferenceType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %4$s<%2$s>(toList(%2$s.class, entity.getProperty(\"%3$s\"))));",
                                    attr.getWriteMethodName(),
                                    type.getClassName(),
                                    attr.getName(),
                                    LinkedHashSet);
                            return true;
                        }

                        @Override
                        public Boolean visitShortType(ShortType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %3$s<%4$s>(longListToShortList(entity.getProperty(\"%2$s\"))));",
                                    attr.getWriteMethodName(),
                                    attr.getName(),
                                    LinkedHashSet,
                                    Short);
                            return true;
                        }

                        @Override
                        public Boolean visitIntegerType(IntegerType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %3$s<%4$s>(longListToIntegerList(entity.getProperty(\"%2$s\"))));",
                                    attr.getWriteMethodName(),
                                    attr.getName(),
                                    LinkedHashSet,
                                    Integer);
                            return true;
                        }

                        @Override
                        public Boolean visitFloatType(FloatType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %3$s<%4$s>(doubleListToFloatList(entity.getProperty(\"%2$s\"))));",
                                    attr.getWriteMethodName(),
                                    attr.getName(),
                                    LinkedHashSet,
                                    Float);
                            return true;
                        }

                        @Override
                        public Boolean visitEnumType(EnumType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %4$s<%2$s>(stringListToEnumList(%2$s.class, entity.getProperty(\"%3$s\"))));",
                                    attr.getWriteMethodName(),
                                    type.getTypeName(),
                                    attr.getName(),
                                    LinkedHashSet);
                            return true;
                        }
                    },
                    null);
            return handled ? null : super.visitSetType(collectionType, attr);
        }

        @Override
        public Void visitSortedSetType(final SortedSetType collectionType,
                final AttributeMetaDesc attr) throws RuntimeException {
            DataType elementType = collectionType.getElementType();
            Boolean handled =
                elementType.accept(
                    new SimpleDataTypeVisitor<Boolean, Void, RuntimeException>(
                        false) {

                        @Override
                        public Boolean visitCoreReferenceType(
                                CoreReferenceType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %4$s<%2$s>(toList(%2$s.class, entity.getProperty(\"%3$s\"))));",
                                    attr.getWriteMethodName(),
                                    type.getClassName(),
                                    attr.getName(),
                                    TreeSet);
                            return true;
                        }

                        @Override
                        public Boolean visitShortType(ShortType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %3$s<%4$s>(longListToShortList(entity.getProperty(\"%2$s\"))));",
                                    attr.getWriteMethodName(),
                                    attr.getName(),
                                    TreeSet,
                                    Short);
                            return true;
                        }

                        @Override
                        public Boolean visitIntegerType(IntegerType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %3$s<%4$s>(longListToIntegerList(entity.getProperty(\"%2$s\"))));",
                                    attr.getWriteMethodName(),
                                    attr.getName(),
                                    TreeSet,
                                    Integer);
                            return true;
                        }

                        @Override
                        public Boolean visitFloatType(FloatType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %3$s<%4$s>(doubleListToFloatList(entity.getProperty(\"%2$s\"))));",
                                    attr.getWriteMethodName(),
                                    attr.getName(),
                                    TreeSet,
                                    Float);
                            return true;
                        }

                        @Override
                        public Boolean visitEnumType(EnumType type, Void p)
                                throws RuntimeException {
                            printer
                                .println(
                                    "model.%1$s(new %4$s<%2$s>(stringListToEnumList(%2$s.class, entity.getProperty(\"%3$s\"))));",
                                    attr.getWriteMethodName(),
                                    type.getTypeName(),
                                    attr.getName(),
                                    TreeSet);
                            return true;
                        }
                    },
                    null);
            return handled ? null : super.visitSortedSetType(
                collectionType,
                attr);
        }
    }

    /**
     * Represents the {@code modelToMethod} method generator.
     * 
     * @author taedium
     * @since 1.0.0
     * 
     */
    protected class ModelToEntityMethodGenerator extends
            SimpleDataTypeVisitor<Void, AttributeMetaDesc, RuntimeException> {

        /** the printer */
        protected final Printer printer;

        /**
         * Creates a new {@link ModelToEntityMethodGenerator}.
         * 
         * @param printer
         *            the printer
         */
        public ModelToEntityMethodGenerator(Printer printer) {
            this.printer = printer;
        }

        /**
         * Generates the modelToMethod method.
         */
        public void generate() {
            printer.println("@Override");
            printer.println(
                "public %1$s modelToEntity(%2$s model) {",
                Entity,
                Object);
            printer.indent();
            if (modelMetaDesc.isAbstrct()) {
                printer.println(
                    "throw new %1$s(\"The class(%2$s) is abstract.\");",
                    UnsupportedOperationException.class.getName(),
                    modelMetaDesc.getModelClassName());
            } else {
                printer.println(
                    "%1$s m = (%1$s) model;",
                    modelMetaDesc.getModelClassName());
                printer.println("%1$s entity = null;", Entity);
                printer.println("if (m.%1$s() != null) {", modelMetaDesc
                    .getKeyAttributeMetaDesc()
                    .getReadMethodName());
                printer
                    .println(
                        "    entity = new %1$s(m.%2$s());",
                        Entity,
                        modelMetaDesc
                            .getKeyAttributeMetaDesc()
                            .getReadMethodName());
                printer.println("} else {");
                printer.println("    entity = new %1$s(kind);", Entity);
                printer.println("}");
                for (AttributeMetaDesc attr : modelMetaDesc
                    .getAttributeMetaDescList()) {
                    if (!attr.isPersistent()) {
                        continue;
                    }
                    if (attr.isPrimaryKey()) {
                        continue;
                    }
                    DataType dataType = attr.getDataType();
                    dataType.accept(this, attr);
                }
                int schemaVersion = modelMetaDesc.getSchemaVersion();
                if (schemaVersion > 0) {
                    printer.println(
                        "entity.setProperty(\"%1$s\", %2$s);",
                        modelMetaDesc.getSchemaVersionName(),
                        modelMetaDesc.getSchemaVersion());
                }
                if (!modelMetaDesc.getClassHierarchyList().isEmpty()) {
                    printer.println(
                        "entity.setProperty(\"%1$s\", classHierarchyList);",
                        modelMetaDesc.getClassHierarchyListName());
                }
                printer.println("return entity;");
            }
            printer.unindent();
            printer.println("}");
            printer.println();
        }

        @Override
        protected Void defaultAction(DataType type, AttributeMetaDesc p)
                throws RuntimeException {
            printer
                .println(
                    "entity.setUnindexedProperty(\"%1$s\", serializableToBlob(m.%2$s()));",
                    p.getName(),
                    p.getReadMethodName());
            return null;
        }

        @Override
        public Void visitCorePrimitiveType(CorePrimitiveType type,
                AttributeMetaDesc p) throws RuntimeException {
            if (p.isUnindexed()) {
                printer.println(
                    "entity.setUnindexedProperty(\"%1$s\", m.%2$s());",
                    p.getName(),
                    p.getReadMethodName());
            } else {
                printer.println(
                    "entity.setProperty(\"%1$s\", m.%2$s());",
                    p.getName(),
                    p.getReadMethodName());
            }
            return null;
        }

        @Override
        public Void visitCoreReferenceType(CoreReferenceType type,
                AttributeMetaDesc p) throws RuntimeException {
            if (p.isUnindexed()) {
                printer.println(
                    "entity.setUnindexedProperty(\"%1$s\", m.%2$s());",
                    p.getName(),
                    p.getReadMethodName());
            } else {
                printer.println(
                    "entity.setProperty(\"%1$s\", m.%2$s());",
                    p.getName(),
                    p.getReadMethodName());
            }
            return null;
        }

        @Override
        public Void visitStringType(StringType type, AttributeMetaDesc p)
                throws RuntimeException {
            if (p.isLob() && p.isCipher()) {
                printer
                    .println(
                        "entity.setUnindexedProperty(\"%1$s\", stringToText(encrypt(m.%2$s())));",
                        p.getName(),
                        p.getReadMethodName());
                return null;
            }
            if (p.isLob()) {
                printer
                    .println(
                        "entity.setUnindexedProperty(\"%1$s\", stringToText(m.%2$s()));",
                        p.getName(),
                        p.getReadMethodName());
                return null;
            }
            if (p.isCipher()) {
                printer.println(
                    "entity.setProperty(\"%1$s\", encrypt(m.%2$s()));",
                    p.getName(),
                    p.getReadMethodName());
                return null;
            }
            return super.visitStringType(type, p);
        }

        @Override
        public Void visitEnumType(EnumType type, AttributeMetaDesc p)
                throws RuntimeException {
            if (p.isUnindexed()) {
                printer
                    .println(
                        "entity.setUnindexedProperty(\"%1$s\", enumToString(m.%2$s()));",
                        p.getName(),
                        p.getReadMethodName());
            } else {
                printer.println(
                    "entity.setProperty(\"%1$s\", enumToString(m.%2$s()));",
                    p.getName(),
                    p.getReadMethodName());
            }
            return null;
        }

        @Override
        public Void visitTextType(TextType type, AttributeMetaDesc p)
                throws RuntimeException {
            if (p.isCipher()) {
                printer
                    .println(
                        "entity.setUnindexedProperty(\"%1$s\", encrypt(m.%2$s()));",
                        p.getName(),
                        p.getReadMethodName());
                return null;
            }
            printer.println(
                "entity.setUnindexedProperty(\"%1$s\", m.%2$s());",
                p.getName(),
                p.getReadMethodName());
            return null;
        }

        @Override
        public Void visitModelRefType(ModelRefType type, AttributeMetaDesc p)
                throws RuntimeException {
            printer.println("if (m.%1$s() == null) {", p.getReadMethodName());
            printer
                .println(
                    "    throw new NullPointerException(\"The property(%1$s) must not be null.\");",
                    p.getAttributeName());
            printer.println("}");
            if (p.isUnindexed()) {
                printer
                    .println(
                        "entity.setUnindexedProperty(\"%1$s\", m.%2$s().getKey());",
                        p.getName(),
                        p.getReadMethodName());
            } else {
                printer.println(
                    "entity.setProperty(\"%1$s\", m.%2$s().getKey());",
                    p.getName(),
                    p.getReadMethodName());
            }
            return null;
        }

        @Override
        public Void visitArrayType(ArrayType type, final AttributeMetaDesc attr)
                throws RuntimeException {
            DataType componentType = type.getComponentType();
            boolean accepted =
                componentType.accept(
                    new SimpleDataTypeVisitor<Boolean, Void, RuntimeException>(
                        false) {

                        @Override
                        public Boolean visitPrimitiveByteType(
                                PrimitiveByteType type, Void p)
                                throws RuntimeException {
                            if (attr.isLob()) {
                                printer
                                    .println(
                                        "entity.setUnindexedProperty(\"%1$s\", bytesToBlob(m.%2$s()));",
                                        attr.getName(),
                                        attr.getReadMethodName());
                            } else {
                                if (attr.isUnindexed()) {
                                    printer
                                        .println(
                                            "entity.setUnindexedProperty(\"%1$s\", bytesToShortBlob(m.%2$s()));",
                                            attr.getName(),
                                            attr.getReadMethodName());
                                } else {
                                    printer
                                        .println(
                                            "entity.setProperty(\"%1$s\", bytesToShortBlob(m.%2$s()));",
                                            attr.getName(),
                                            attr.getReadMethodName());
                                }
                            }
                            return true;
                        }
                    },
                    null);
            if (accepted) {
                return null;
            }
            return super.visitArrayType(type, attr);
        }

        @Override
        public Void visitListType(ListType type, final AttributeMetaDesc attr)
                throws RuntimeException {
            if (attr.isLob()) {
                return super.visitCollectionType(type, attr);
            }
            DataType componentType = type.getElementType();
            boolean accepted =
                componentType.accept(
                    new SimpleDataTypeVisitor<Boolean, Void, RuntimeException>(
                        false) {

                        @Override
                        public Boolean visitEnumType(EnumType type, Void p)
                                throws RuntimeException {
                            if (attr.isUnindexed()) {
                                printer
                                    .println(
                                        "entity.setUnindexedProperty(\"%1$s\", enumListToStringList(m.%2$s()));",
                                        attr.getName(),
                                        attr.getReadMethodName());
                            } else {
                                printer
                                    .println(
                                        "entity.setProperty(\"%1$s\", enumListToStringList(m.%2$s()));",
                                        attr.getName(),
                                        attr.getReadMethodName());
                            }
                            return true;
                        }
                    },
                    null);
            if (accepted) {
                return null;
            }
            if (attr.isUnindexed()) {
                printer.println(
                    "entity.setUnindexedProperty(\"%1$s\", m.%2$s());",
                    attr.getName(),
                    attr.getReadMethodName());
            } else {
                printer.println(
                    "entity.setProperty(\"%1$s\", m.%2$s());",
                    attr.getName(),
                    attr.getReadMethodName());
            }
            return null;
        }

        @Override
        public Void visitCollectionType(CollectionType type,
                final AttributeMetaDesc attr) throws RuntimeException {
            if (attr.isLob()) {
                return super.visitCollectionType(type, attr);
            }
            DataType componentType = type.getElementType();
            boolean accepted =
                componentType.accept(
                    new SimpleDataTypeVisitor<Boolean, Void, RuntimeException>(
                        false) {

                        @Override
                        public Boolean visitEnumType(EnumType type, Void p)
                                throws RuntimeException {
                            if (attr.isUnindexed()) {
                                printer
                                    .println(
                                        "entity.setUnindexedProperty(\"%1$s\", enumListToStringList(new %3$s<%4$s>(m.%2$s())));",
                                        attr.getName(),
                                        attr.getReadMethodName(),
                                        ArrayList,
                                        type.getTypeName());
                            } else {
                                printer
                                    .println(
                                        "entity.setProperty(\"%1$s\", enumListToStringList(new %3$s<%4$s>(m.%2$s())));",
                                        attr.getName(),
                                        attr.getReadMethodName(),
                                        ArrayList,
                                        type.getTypeName());
                            }
                            return true;
                        }
                    },
                    null);
            if (accepted) {
                return null;
            }
            if (attr.isUnindexed()) {
                printer.println(
                    "entity.setUnindexedProperty(\"%1$s\", m.%2$s());",
                    attr.getName(),
                    attr.getReadMethodName());
            } else {
                printer.println(
                    "entity.setProperty(\"%1$s\", m.%2$s());",
                    attr.getName(),
                    attr.getReadMethodName());
            }
            return null;
        }
    }

    /**
     * Represents the {@code modelToMethod} method generator.
     * 
     * @author taedium
     * @since 1.0.0
     * 
     */
    protected class AssignKeyToModelRefIfNecessaryMethodGenerator extends
            SimpleDataTypeVisitor<Void, AttributeMetaDesc, RuntimeException> {

        /** the printer */
        protected final Printer printer;

        /**
         * Constructor.
         * 
         * @param printer
         *            the printer
         */
        public AssignKeyToModelRefIfNecessaryMethodGenerator(Printer printer) {
            this.printer = printer;
        }

        /**
         * Generates the modelToMethod method.
         */
        public void generate() {
            printer.println("@Override");
            printer
                .println(
                    "protected void assignKeyToModelRefIfNecessary(%1$s ds, %2$s model) {",
                    AsyncDatastoreService,
                    Object);
            printer.indent();
            if (modelMetaDesc.isAbstrct()) {
                printer.println(
                    "throw new %1$s(\"The class(%2$s) is abstract.\");",
                    UnsupportedOperationException.class.getName(),
                    modelMetaDesc.getModelClassName());
            } else {
                boolean found = false;
                for (AttributeMetaDesc attr : modelMetaDesc
                    .getAttributeMetaDescList()) {
                    if (!attr.isPersistent()) {
                        continue;
                    }
                    if (attr.getDataType() instanceof ModelRefType) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    printer.println(
                        "%1$s m = (%1$s) model;",
                        modelMetaDesc.getModelClassName());
                    for (AttributeMetaDesc attr : modelMetaDesc
                        .getAttributeMetaDescList()) {
                        if (!attr.isPersistent()) {
                            continue;
                        }
                        if (attr.isPrimaryKey()) {
                            continue;
                        }
                        DataType dataType = attr.getDataType();
                        dataType.accept(this, attr);
                    }
                }
            }
            printer.unindent();
            printer.println("}");
            printer.println();
        }

        @Override
        protected Void defaultAction(DataType type, AttributeMetaDesc p)
                throws RuntimeException {
            return null;
        }

        @Override
        public Void visitModelRefType(ModelRefType type, AttributeMetaDesc p)
                throws RuntimeException {
            printer.println("if (m.%1$s() == null) {", p.getReadMethodName());
            printer
                .println(
                    "    throw new NullPointerException(\"The property(%1$s) must not be null.\");",
                    p.getAttributeName());
            printer.println("}");
            printer.println(
                "m.%1$s().assignKeyIfNecessary(ds);",
                p.getReadMethodName());
            return null;
        }
    }

    private static final Map<String, String> defaultsOfPrimitives =
        new HashMap<String, String>();

}
