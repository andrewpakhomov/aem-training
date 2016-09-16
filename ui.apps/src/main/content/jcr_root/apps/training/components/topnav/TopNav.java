package apps.training.components.topnav;
import java.util.*;
import java.util.Iterator;
import com.adobe.cq.sightly.WCMUse;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;

public class TopNav extends WCMUse {
    private List<Page> items = new ArrayList<Page>();
    // Initializes the navigation
    public void activate() throws Exception {

        final Page rootPage = getCurrentPage().getAbsoluteParent(1);
        if (rootPage != null) {
            Iterator<Page> childPages = rootPage.listChildren(new PageFilter(getRequest()));
            while (childPages.hasNext()) {
                items.add(childPages.next());
            }
        }
    }
    // Returns the navigation items
    public List<Page> getItems() {
        return items;
    }
}