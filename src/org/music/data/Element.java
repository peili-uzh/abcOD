package org.music.data;

public abstract class Element {

    protected String tag;
    protected String value;

    public abstract void addAttribute(String name, String value);

    public abstract void addChild(Element e);

}
