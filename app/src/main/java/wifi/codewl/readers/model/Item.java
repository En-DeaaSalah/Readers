package wifi.codewl.readers.model;

public class Item {

    private int itemType;
    Object item;

    public Item(int itemType, Object item) {
        this.itemType = itemType;
        this.item = item;
    }

    public Object getItem() {
        return item;
    }

    public int getItemType() {
        return itemType;
    }
}
