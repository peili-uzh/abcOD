package org.music.data;

public class ReleaseElement extends Element {

    public int id;
    public String title;
    public ReleaseArtistData artist;

    @Override
    public void addAttribute(String name, String value) {
        if (name.equalsIgnoreCase("id")) {
            id = Integer.parseInt(value);
        }

    }

    @Override
    public void addChild(Element e) {
        if (e.tag.equalsIgnoreCase("title")) {
            title = e.value;
        }
    }

}
