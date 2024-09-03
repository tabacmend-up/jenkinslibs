def call(boolean Reachability) { 
    echo 'Run Mend dependencies scan'
    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
        if (Reachability) {
            sh '''
            export repo=$(basename -s .git $(git config --get remote.origin.url))
            export branch=$(git rev-parse --abbrev-ref HEAD)
            ./mend dep -u -r -s "*//${JOB_NAME}//${repo}_${branch}" --fail-policy --non-interactive --export-results dep-results.txt'
            '''
        } else {
            sh '''
            export repo=$(basename -s .git $(git config --get remote.origin.url))
            export branch=$(git rev-parse --abbrev-ref HEAD)
            ./mend dep -u -s "*//${JOB_NAME}//${repo}_${branch}" --fail-policy --non-interactive --export-results dep-results.txt'
            '''
        }
    }
}

            // export repo=$(basename -s .git $(git config --get remote.origin.url))
            // export branch=$(git rev-parse --abbrev-ref HEAD)
            // if $Reachability; then
                  
            // else
            //        ./mend dep -u -s "*//${JOB_NAME}//${repo}_${branch} --fail-policy" --non-interactive --export-results dep-results.txt
            // fi
            sh '''
            if [[ "$dep_exit" == "9" ]]; then
                  echo "[warning]  Dependency scan policy violation"
            else
                  echo "No policy violations found in dependencies scan"
            fi          
            '''
      }
      archiveArtifacts artifacts: "dep-results.txt", fingerprint: true

}
