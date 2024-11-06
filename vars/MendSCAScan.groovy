def call(Map args = [:]) { 
    boolean reachability = args.get('reachability', false)
    echo 'Run Mend dependencies scan'

    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
        // Construct the Mend command based on reachability
        def reachabilityFlag = reachability ? "-r" : ""

        // Execute the Mend command along with environment setup
        sh """
        export repo=\$(basename -s .git \$(git config --get remote.origin.url))
        export branch=\$(git rev-parse --abbrev-ref HEAD)
        
        ./mend dep -u ${reachabilityFlag} -s "*//\${JOB_NAME}//\${repo}_\${branch}" --fail-policy --non-interactive --export-results dep-results.txt

        dep_exit=\$?
        if [[ "\$dep_exit" == "9" ]]; then
            echo "[warning] Dependency scan policy violation"
        else
            echo "No policy violations found in dependencies scan"
        fi
        """
    }

    // Archive the results
    archiveArtifacts artifacts: "dep-results.txt", fingerprint: true
}
