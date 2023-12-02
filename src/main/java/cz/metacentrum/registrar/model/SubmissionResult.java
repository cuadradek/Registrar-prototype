package cz.metacentrum.registrar.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SubmissionResult {
	private Submission submission;
	private List<String> messages = new ArrayList<>();
	private Submission redirectSubmission;
	private String redirectUrl;

	public void addMessage(String message) {
		messages.add(message);
	}
}
