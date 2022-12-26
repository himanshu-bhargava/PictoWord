package com.sb.play.pictoword;

public class RoundDetail {
    private String firstImage;
    private String secondImage;
    private String answer;

    public String getFirstImage() {
        return firstImage;
    }

    public void setFirstImage(String firstImage) {
        this.firstImage = firstImage;
    }

    public String getSecondImage() {
        return secondImage;
    }

    public void setSecondImage(String secondImage) {
        this.secondImage = secondImage;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "RoundDetail{" +
                "firstImage='" + firstImage + '\'' +
                ", secondImage='" + secondImage + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}