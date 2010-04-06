/*
 * Copyright 2004-2009 the original author or authors.
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
package org.slim3.gen.util;

import java.util.regex.Pattern;

import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.type.PrimitiveType.Kind;

/**
 * A utility class for {@link FieldDeclaration}.
 * 
 * @author taedium
 * 
 */
public final class FieldDeclarationUtil {

    private static Pattern isPrefixedFieldPattern =
        Pattern.compile("^is[A-Z].*");

    /**
     * Get the read method name.
     * 
     * @param fieldDeclaration
     *            the field type
     * @return the read method name
     */
    public static String getReadMethodName(FieldDeclaration fieldDeclaration) {
        if (fieldDeclaration == null) {
            throw new NullPointerException(
                "The fieldDeclaration parameter is null.");
        }
        String fieldName = fieldDeclaration.getSimpleName();
        if (TypeUtil.isPrimitive(fieldDeclaration.getType(), Kind.BOOLEAN)) {
            if (isPrefixedFieldPattern.matcher(fieldName).matches()) {
                return fieldName;
            }
            return "is" + StringUtil.capitalize(fieldName);
        }
        return "get" + StringUtil.capitalize(fieldName);
    }

    /**
     * Get the write method name.
     * 
     * @param fieldDeclaration
     *            the field type
     * @return the write method name
     */
    public static String getWriteMethodName(FieldDeclaration fieldDeclaration) {
        if (fieldDeclaration == null) {
            throw new NullPointerException(
                "The fieldDeclaration parameter is null.");
        }
        String fieldName = fieldDeclaration.getSimpleName();
        if (TypeUtil.isPrimitive(fieldDeclaration.getType(), Kind.BOOLEAN)) {
            if (isPrefixedFieldPattern.matcher(fieldName).matches()) {
                return "set" + fieldName.substring(2);
            }
        }
        return "set" + StringUtil.capitalize(fieldName);
    }
}
