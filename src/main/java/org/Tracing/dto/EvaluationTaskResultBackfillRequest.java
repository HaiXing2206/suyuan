package org.Tracing.dto;

import java.math.BigDecimal;

public class EvaluationTaskResultBackfillRequest {
    private BigDecimal resultScore;
    private String resultGrade;
    private String issueList;

    public BigDecimal getResultScore() {
        return resultScore;
    }

    public void setResultScore(BigDecimal resultScore) {
        this.resultScore = resultScore;
    }

    public String getResultGrade() {
        return resultGrade;
    }

    public void setResultGrade(String resultGrade) {
        this.resultGrade = resultGrade;
    }

    public String getIssueList() {
        return issueList;
    }

    public void setIssueList(String issueList) {
        this.issueList = issueList;
    }
}
