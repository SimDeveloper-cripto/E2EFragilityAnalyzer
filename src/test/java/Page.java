
import org.jsoup.nodes.Document;

public class Page {
    private Document page;
    private float pageComplexity, pageScore;

    public Page(Document page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "PageComplexityScore = " + getPageComplexity()  + "  ";
    }

    public void setPage(Document page) {
        this.page = page;
    }

    public Document getPage() {
        return this.page;
    }

    public float getPageScore() {
        return this.pageScore;
    }

    public void setPageScore(float pageScore) {
        this.pageScore = pageScore;
    }

    public float getPageComplexity() {
        return this.pageComplexity;
    }

    public void setPageComplexity(float pageComplexity) {
        this.pageComplexity = pageComplexity;
    }
}