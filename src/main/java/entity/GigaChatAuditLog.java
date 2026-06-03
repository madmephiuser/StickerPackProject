/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "gigachat_audit_logs")
public class GigaChatAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String prompt;
    
    private String status;
    private int statusCode;
    
    @Column(length = 4000)
    private String responseOrError;

    public GigaChatAuditLog() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    public String getResponseOrError() { return responseOrError; }
    public void setResponseOrError(String responseOrError) { this.responseOrError = responseOrError; }
}
