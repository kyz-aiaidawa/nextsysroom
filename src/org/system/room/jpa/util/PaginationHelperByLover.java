/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.system.room.jpa.util;

/**
 *
 * @author lisa
 */
public abstract class PaginationHelperByLover {

    private int pageSize; // 1page の　行数
    private int page;       // Oからカウント

    public PaginationHelperByLover(int pageSize) {
        this.pageSize = pageSize;
    }

    public abstract int getItemsCount();

  //  public abstract DataModel createPageDataModel();

    public int getPageFirstItem() {
        return page * pageSize;
    }

    public int getPageLastItem() {
        int i = getPageFirstItem() + pageSize - 1;
        int count = getItemsCount() - 1;
        if (i > count) {
            i = count;
        }
        if (i < 0) {
            i = 0;
        }
        return i;
    }

    public boolean isHasNextPage() {
        return (page + 1) * pageSize + 1 <= getItemsCount();
    }
    public boolean isThisPageByLover() {
        System.out.println("isThisPage() " + page);
        return (page) * pageSize + 1 <= getItemsCount();
    }
    public void addPageByLover(){
        page++;
    }
    public void nextPage() {
        //System.out.println("nextPage()" + page);
        if (isHasNextPage()) {
            page++;
        }
    }

    public boolean isHasPreviousPage() {
        return page > 0;
    }

    public void previousPage() {
        if (isHasPreviousPage()) {
            page--;
        }
    }

    public int getPageSize() {
        return pageSize;
    }
}
