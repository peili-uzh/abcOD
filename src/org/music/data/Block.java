package org.music.data;

import java.util.ArrayList;

public class Block {

    protected int block_id;
    protected ArrayList entities;

    public int getBlock_id() {
        return block_id;
    }

    public void setBlock_id(int block_id) {
        this.block_id = block_id;
    }

    public ArrayList getEntities() {
        return entities;
    }

    public void setEntities(ArrayList entities) {
        this.entities = entities;
    }

    public Block() {

    }

    public Block(int block_id, ArrayList entities) {
        this.block_id = block_id;
        this.entities = entities;
    }

}
