package tabCompletion.vo;

public class ChoiceVO {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "ChoiceVO{" +
                "text='" + text + '\'' +
                '}';
    }
}
