package com.yyy.batch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public enum ColType {
    INTEGER {
        @Override
        Object parseString(String value) {
            return Long.parseLong(value);
        }
    },
    STRING {
        @Override
        Object parseString(String value) {
            return value;
        }
    },
    STRING_ARRAY {
        @Override
        Object parseString(String value) {
            String[] arr = value.split(",");
            try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
                try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                    o.writeObject(arr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return b.toByteArray();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return new byte[0];
        }
    };
    abstract Object parseString(String value);
}