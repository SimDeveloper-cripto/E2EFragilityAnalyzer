import org.jsoup.nodes.Document;

public class Page {
    private Document page;
    private float pageComplexity, pageScore;

    public Page(Document page) {
        this.page = page;
    }

    public Document getPage() {
        return page;
    }

    @Override
    public String toString() {
        return "PageComplexity = " + pageComplexity  + "\n";
    }
    public float getPageScore() {
        return pageScore;
    }
    public void setPageScore(float pageScore) {
        this.pageScore = pageScore;
    }
    public float getPageComplexity() {
        return pageComplexity;
    }

    public void setPageComplexity(float pageComplexity) {
        this.pageComplexity = pageComplexity;
    }
}