package io.genreadme.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class GitLabWebhookEvent {
 @SerializedName("object_kind")
 private String objectKind;
 
 @SerializedName("project_id") 
 private Long projectId;
 
 private String ref;
 private GitLabRepository repository;
 private List<GitLabCommit> commits;
}