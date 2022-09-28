package me.errordev.event.handling;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Target {

    Priority value() default Priority.LOW;

}