# Troubleshooting — Issues and Fixes

## 1. Docker Permission Denied
Error: permission denied while trying to connect to /var/run/docker.sock
Cause: Jenkins user not in docker group
Fix: Run these commands on the server:
    sudo usermod -aG docker jenkins
    sudo chmod 666 /var/run/docker.sock
    sudo systemctl restart jenkins

---

## 2. Script Not Approved
Error: ERROR: script not yet approved for use
Cause: Jenkins sandbox blocked the DSL script
Fix: Manage Jenkins > Security > In-process Script Approval > Approve
Note: Reoccurs every time the seed job script content changes

---

## 3. Unknown Output Type Error
Error: Unknown output type backslash
Cause: Multi-line shell variable using backslash continuation inside sh block
Fix: Use single-line commands and redirect output to temp file
    Wrong:  MANIFEST=$(aws ecr batch-get-image --output text)
    Correct: aws ecr batch-get-image --output text > /tmp/manifest.json

---

## 4. ECR Retag Stage False Failure
Error: Pipeline shows failure but image is actually retagged successfully in ECR
Cause: aws ecr put-image returns non-zero exit when image tag already exists
Fix: Append || true to make the command idempotent
    aws ecr put-image ... || true

---

## 5. Invalid ECR Image Tag
Error: Image pushed with tag devV* instead of devV1
Cause: Parameter default value left as placeholder and used literally
Fix: Always set a real value (v1, v2, v3) in releaseTag parameter before building

---

## 6. Groovy DSL Variable Name Conflict
Error: No signature of method: java.lang.String.call()
Cause: Local variable named 'script' conflicts with DSL method script()
Fix: Rename variable to pipelineScript
    Wrong:   def script = "pipeline { ... }"
    Correct: def pipelineScript = "pipeline { ... }"
