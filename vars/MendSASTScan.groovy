def call() { 
   echo 'Start Mend Code Scan'
   catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE'){
      sh '''
      export repo=$(basename -s .git $(git config --get remote.origin.url))
      export branch=$(git rev-parse --abbrev-ref HEAD)
      export MEND_SAST_REPORT_TYPE="OWASP2021"
      ./mend code --non-interactive -s "*//${JOB_NAME}//${repo}_${branch}" -r --formats pdf --filename code-results
      if [[ "$code_exit" == "9" ]]; then
        echo "[warning] Code scan threshold violation"
      else
        echo "No policy violations found in code scan"
      fi
      '''
   archiveArtifacts artifacts: "code-results.pdf", fingerprint: true
 }
}
