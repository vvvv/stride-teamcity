package Engine.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.powerShell

object Engine_BuildWarningReportGenerator : BuildType({
    name = "BuildWarningReportGenerator"

    steps {
        powerShell {
            name = "Build Warning Report Generator"
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    ${'$'}BuildLogPath = %BuildLogPath%
                    ${'$'}BuildCheckoutDirectoryPath = %BuildCheckoutDirectoryPath%
                    ${'$'}BuildArtifactRepositoryUrl = "%teamcity.serverUrl%/repository/download/%system.teamcity.buildType.id%/"
                    
                    <#
                    BuildWarningReportGenerator.ps1
                    Processes an MSBuild log, and creates a report of build warnings; integrates with TeamCity
                    
                    Local Test:
                    .\BuildWarningReportGenerator.ps1 -BuildLogPath "<path to build log directory>\BuildLog.log" -BuildCheckoutDirectoryPath "<path to build checkout directory>\" -BuildArtifactRepositoryUrl "http://<server:port>/repository/download/<build configuration id>/"
                    #>
                    
                    Write-Host
                    Write-Host "================================================================================"
                    Write-Host ${'$'}MyInvocation.MyCommand.Name
                    Write-Host
                    
                    
                    
                    Add-Type -AssemblyName System.Web
                    Add-Type -TypeDefinition @"
                    using System.Text.RegularExpressions;
                    public class BuildWarning
                    {
                        public string Solution { get; private set; }
                        public string Project { get; private set; }
                        public string WarningMessage { get; private set; }
                        public string WarningCode { get; private set; }
                        public string Key { get; private set; }
                        public bool IsNew { get; set; }
                    
                        private static readonly Regex warningMessageKeyRegex = new Regex(@"^(?<before>.*)\([0-9,]+\)(?<after>: warning .*)${'$'}");
                    
                        public BuildWarning(string solution, string project, string warningMessage, string warningCode)
                        {
                            Solution = solution;
                            Project = project;
                            WarningMessage = warningMessage;
                            WarningCode = warningCode;
                    
                            var match = warningMessageKeyRegex.Match(WarningMessage);
                            Key = Solution + "|" + Project + "|" + match.Groups["before"].Value + match.Groups["after"].Value;
                        }
                    }
                    "@
                    
                    
                    
                    Write-Host "============================================================"
                    Write-Host "Getting warnings"
                    Write-Host
                    
                    [string] ${'$'}sln
                    [System.Collections.Generic.Dictionary``2[int,string]] ${'$'}projectStack = New-Object "System.Collections.Generic.Dictionary``2[int,string]"
                    [System.Collections.Generic.List``1[BuildWarning]] ${'$'}warnings = New-Object "System.Collections.Generic.List``1[BuildWarning]"
                    [System.Collections.Generic.List``1[string]] ${'$'}projWarningKeys = New-Object "System.Collections.Generic.List``1[string]"
                    Foreach (${'$'}line in Get-Content -ErrorAction Stop ${'$'}BuildLogPath)
                    {
                        if (${'$'}line -match '^(?<leadingSpace> +)(?<content>\S.*)${'$'}')
                        {
                            [int] ${'$'}i = ${'$'}matches['leadingSpace'].Length / 4
                            [string] ${'$'}content = ${'$'}matches['content']
                    
                            if (${'$'}content -match '^Project .* is building ".*\\(?<solution>.*\.sln)" .*${'$'}')
                            {
                                ${'$'}sln = ${'$'}matches['solution']
                                ${'$'}warnings += New-Object BuildWarning(${'$'}sln, "", "", "")
                            }
                            elseif (${'$'}content -match '^Project .* is building ".*\\(?<project>.*?)" .*${'$'}')
                            {
                                ${'$'}projectStack[${'$'}i] = ${'$'}matches['project']
                                if (${'$'}projectStack[${'$'}i].EndsWith("tmp_proj"))
                                {
                                    ${'$'}projectStack[${'$'}i] = ${'$'}projectStack[${'$'}i-1]
                                }
                                #${'$'}warnings += New-Object BuildWarning("", ${'$'}projectStack[${'$'}i], "") # for troubleshooting
                                ${'$'}projWarningKeys.Clear()
                            }
                            elseif (${'$'}content -match '^(?<warningMessage>.*warning (?<warningCode>\w*): .*)${'$'}')
                            {
                                [BuildWarning] ${'$'}newWarning = New-Object BuildWarning(${'$'}sln, ${'$'}projectStack[${'$'}i-1], ${'$'}matches['warningMessage'], ${'$'}matches['warningCode'])
                                # avoid duplicates
                                if ( -not ${'$'}projWarningKeys.Contains(${'$'}newWarning.Key))
                                {
                                    ${'$'}warnings += ${'$'}newWarning
                                    ${'$'}projWarningKeys.Add(${'$'}newWarning.Key)
                                }
                            }
                        }
                    }
                    
                    [System.Int32] ${'$'}warningCount = ${'$'}warnings | where { ${'$'}_.WarningMessage -ne "" } | measure | % { ${'$'}_.Count }
                    Write-Host "${'$'}warningCount warnings"
                    
                    # Set TeamCity status/statistic"
                    Write-Host
                    Write-Host "##teamcity[buildStatus text='{build.status.text}; Build warnings: ${'$'}warningCount']"
                    Write-Host "##teamcity[buildStatisticValue key='buildWarnings' value='${'$'}warningCount']"
                    
                    
                    
                    Write-Host
                    Write-Host "============================================================"
                    Write-Host "Writing warnings file"
                    Write-Host
                    
                    # file output
                    [string] ${'$'}warningFileRelativePath = Join-Path ${'$'}BuildCheckoutDirectoryPath "BuildWarnings.txt"
                    Write-Host ("Writing " + ${'$'}warningFileRelativePath)
                    ${'$'}stream = [System.IO.StreamWriter] (Join-Path (pwd) ${'$'}warningFileRelativePath )
                    ${'$'}stream.WriteLine("#Build Warnings")
                    ${'$'}stream.WriteLine("#" + ${'$'}warningCount + " warnings")
                    ${'$'}stream.WriteLine("#Generated " + (Get-Date -format g))
                    ${'$'}stream.WriteLine("#====================================")
                    ${'$'}warnings | where { ${'$'}_.WarningMessage -ne "" } | Sort-Object Key | foreach { ${'$'}stream.WriteLine(${'$'}_.Key) }
                    ${'$'}stream.Close()
                    
                    # Publish as artifact (needs to be visible to allow guest download)
                    Write-Host ("##teamcity[publishArtifacts 'BuildWarnings.txt => BuildWarnings.zip']")
                    
                    
                    
                    # Getting previous warnings
                    if (-not [string]::IsNullOrEmpty(${'$'}BuildArtifactRepositoryUrl)) {
                    
                        Write-Host
                        Write-Host "============================================================"
                        Write-Host "Getting previous warnings, determining new/old"
                        Write-Host
                    
                        [System.Collections.ArrayList] ${'$'}previousWarnings = @()
                    
                        # Retrieve prior warnings; first try last completed build (even if tests failed build, still use that list of warnings)
                        [string] ${'$'}previousWarningsUrl = ${'$'}BuildArtifactRepositoryUrl + ".lastFinished/BuildWarnings.zip!BuildWarnings.txt?guest=1"
                        Write-Host ("Retrieving " + ${'$'}previousWarningsUrl)
                        try
                        {
                            ${'$'}previousWarnings = ((new-object Net.WebClient).DownloadString(${'$'}previousWarningsUrl)) -split "`r`n"
                        }
                        catch
                        {
                            Write-Host ${'$'}Error
                    
                            # Retrieve prior warnings (if lastFinished not found, try lastSuccessful)
                            ${'$'}previousWarningsUrl = ${'$'}BuildArtifactRepositoryUrl + ".lastSuccessful/BuildWarnings.zip!BuildWarnings.txt?guest=1"
                            Write-Host ("Retrieving " + ${'$'}previousWarningsUrl)
                            try
                            {
                                ${'$'}previousWarnings = ((new-object Net.WebClient).DownloadString(${'$'}previousWarningsUrl)) -split "`r`n"
                            }
                            catch
                            {
                                Write-Host ${'$'}Error
                            }
                        }
                    
                        # Remove header block and trailing blank line
                        ${'$'}previousWarnings = ${'$'}previousWarnings | where { -not ${'$'}_.StartsWith("#") -and ${'$'}_ -ne "" }
                    
                        Write-Host ("Previous warnings: " + ${'$'}previousWarnings.Count)
                    
                    
                        # Mark New warnings, count new/old
                        ${'$'}warnings | where { ${'$'}previousWarnings -notcontains ${'$'}_.Key } | % { ${'$'}_.IsNew = ${'$'}true }
                        [int] ${'$'}newCount = ${'$'}warnings | where { ${'$'}_.WarningMessage -ne "" -and ${'$'}_.IsNew } | measure | % { ${'$'}_.Count }
                        [int] ${'$'}oldCount = ${'$'}warningCount - ${'$'}newCount
                        Write-Host
                        Write-Host ("Of the " + ${'$'}warningCount + " current warnings, " + ${'$'}newCount + " are new, " + ${'$'}oldCount + " are old")
                    
                    
                        # Amend TeamCity status
                        Write-Host
                        Write-Host ("##teamcity[buildStatus text='{build.status.text}; Build warnings: ${'$'}warningCount (+" + ${'$'}newCount + "/-" + (${'$'}previousWarnings.Count - ${'$'}oldCount) + ")']")
                    }
                    
                    
                    
                    Write-Host
                    Write-Host "============================================================"
                    Write-Host "Writing warnings report"
                    Write-Host
                    
                    [string] ${'$'}warningReportRelativePath = Join-Path ${'$'}BuildCheckoutDirectoryPath "BuildWarningReport.html"
                    Write-Host ("Writing " + ${'$'}warningReportRelativePath)
                    ${'$'}stream = [System.IO.StreamWriter] (Join-Path (pwd) ${'$'}warningReportRelativePath )
                    ${'$'}stream.WriteLine("<!DOCTYPE html>
                    <html>
                    <head>
                        <!-- relies on TeamCity's jQuery - another option would be to use a CDN -->
                        <script src=`"/js/jquery/jquery-1.11.1.min.js`"></script>
                    
                        <style>
                            body {
                                font: 82%/1.5em `"Helvetica Neue`", Arial, sans-serif;
                                text-rendering: optimizeLegibility;
                            }
                            span.filter {
                                display: inline-block;
                                background-color: #FC0;
                                white-space: nowrap;
                                padding:  1px 6px 0px 4px;
                                border: 1px solid white;
                                border-radius: 6px;
                            }
                            span.filter > input[type=checkbox] { vertical-align: top; }
                            span.filter.outline { border-color: black; }
                            span.filter.red { background-color: #F30; }
                            span.filter.gray { background-color: #CCC; }
                            li.warning.new { color: #F30; }
                            span.filter.hide, .hide-wc, .hide-state { display: none; }
                        </style>
                    
                        <script type=`"text/javascript`">
                    
                            function refreshTree() {
                    
                                // Show all filtered-out projects and collapsed solutions/projects
                                `${'$'}('li.sln, li.proj, li > ul').show();
                    
                                // Hide projects with 0 warnings (based on current filter)
                                if (!`${'$'}('#showAllSolutions').prop('checked'))
                                    `${'$'}('li.sln, li.proj').each(function( index ) {
                                        `${'$'}(this).toggle(`${'$'}(this).find('ul li.warning:visible').length > 0);
                                    });
                    
                                // Update counts (based on current filter)
                                `${'$'}('li.sln, li.proj').each(function( index ) {
                                    `${'$'}(this).find('label').html(`${'$'}(this).find('label').attr('data-value') + ' (' + `${'$'}(this).find('ul li.warning:visible').length + ')');
                                });
                    
                                // Re-collapse collapsed projects
                                `${'$'}('li.collapsed > ul').hide();
                    
                            }
                    
                            `${'$'}(document).ready(function() {
                    
                                // If JS is supported, show filters and initialize counts
                                `${'$'}('.hide').removeClass('hide');
                                refreshTree();
                    
                                // Setup handler for new/old filters
                                `${'$'}('span.filter > input[type=checkbox][data-warning-state]').change(function() {
                                    `${'$'}('.' + `${'$'}(this).attr('data-warning-state')).toggleClass('hide-state', !`${'$'}(this).prop('checked'));
                                    refreshTree();
                                });
                    
                                // Setup handler for 'all/none' checkbox
                                `${'$'}('#allCodes').change(function() {
                                    var checked = `${'$'}(this).prop('checked');
                                    `${'$'}('span.filter > input[type=checkbox][data-code]').prop('checked', checked);
                                    `${'$'}('li[data-code]').toggleClass('hide-wc', !checked);
                                    refreshTree();
                                });
                    
                                // Setup click handlers for filters, to show/hide warnings
                                `${'$'}('span.filter > input[type=checkbox][data-code]').change(function() {
                                    `${'$'}('li[data-code=' + `${'$'}(this).attr('data-code') + ']').toggleClass('hide-wc', !`${'$'}(this).prop('checked'));
                    
                                    // setup 'all/none' checkbox
                                    var checkedCount = `${'$'}('span.filter > input[data-code]:checked').length;
                                    var uncheckedCount = `${'$'}('span.filter > input[data-code]:not(:checked)').length;
                                    `${'$'}('#allCodes').prop('checked', (uncheckedCount == 0));
                                    `${'$'}('#allCodes').prop('indeterminate', (checkedCount > 0 && uncheckedCount > 0));
                    
                                    refreshTree();
                                });
                    
                    
                                `${'$'}('#expandAll').click(function() {
                                    `${'$'}('li.collapsed').removeClass('collapsed');
                                    refreshTree();
                                });
                                `${'$'}('#collapseAll').click(function() {
                                    `${'$'}('li.sln, li.proj').addClass('collapsed');
                                    refreshTree();
                                });
                    
                    
                                `${'$'}('#showAllSolutions').change(function() {
                                    refreshTree();
                                });
                    
                                // Setup click handler for Solution/Project headers, to collapse/expand
                                `${'$'}('li > label').click(function() {
                                    `${'$'}(this).parent().toggleClass('collapsed');
                                    refreshTree();
                                });
                    
                                // Setup click handler for warning, to highlight the corresponding filter
                                `${'$'}('li.warning').click(function(ev) {
                                    `${'$'}('.outline').removeClass('outline');
                                    `${'$'}('span.filter > input[data-code=' + `${'$'}(this).attr('data-code') + ']').parent().addClass('outline');
                                    ev.stopPropagation();
                                });
                                // Setup click handler for document, to remove highlight
                                `${'$'}(document).click(function() {
                                    `${'$'}('.outline').removeClass('outline');
                                });
                    
                            });
                    
                        </script>
                    
                    </head>
                    <body>
                      <h1>Build Warnings (${'$'}warningCount)</h1>")
                    
                    # Filters
                    if (${'$'}warningCount -gt 0)
                    {
                        ${'$'}stream.WriteLine("  <div>")
                        if ((${'$'}newCount -gt 0) -and (${'$'}oldCount -gt 0))
                        {
                            ${'$'}stream.WriteLine("    <span class='filter hide red'><input type='checkbox' checked='checked' data-warning-state='new' />New (${'$'}newCount)</span>")
                            ${'$'}stream.WriteLine("    <span class='filter hide gray'><input type='checkbox' checked='checked' data-warning-state='old' />Old (${'$'}oldCount)</span>")
                        }
                        ${'$'}stream.WriteLine("    <span class='filter hide'><input type='checkbox' checked='checked' id='allCodes' /></span>")
                        ${'$'}warnings | Group-Object WarningCode | where { ${'$'}_.Name -ne "" } | sort Count -Descending | foreach { "<span class='filter hide'><input type='checkbox' checked='checked' data-code='${'$'}(${'$'}_.Name)'/>${'$'}(${'$'}_.Name) (${'$'}(${'$'}_.Count))</span>" } | foreach { ${'$'}stream.WriteLine("    " + ${'$'}_) }
                        ${'$'}stream.WriteLine("    <span class='filter hide gray' title='Expand all solutions and projects' id='expandAll'>Expand All</span>")
                        ${'$'}stream.WriteLine("    <span class='filter hide gray' title='Collapse all solutions and projects' id='collapseAll'>Collapse All</span>")
                        ${'$'}stream.WriteLine("    <span class='filter hide gray' title='Show solutions with no warnings'><input type='checkbox' id='showAllSolutions' />Show All Solutions</span>")
                        ${'$'}stream.WriteLine("  </div>")
                    }
                    
                    # Warnings hierarchy
                    ${'$'}stream.WriteLine("  <ul>")
                    [string] ${'$'}currSln = ""
                    [string] ${'$'}currProj = ""
                    foreach (${'$'}warning in ${'$'}warnings)
                    {
                        # for troubleshooting
                        #${'$'}stream.WriteLine("    <!-- ${'$'}currSln|${'$'}currProj - ${'$'}("${'$'}(${'$'}warning.IsNew)|${'$'}(${'$'}warning.Key)")-->")
                    
                        # Check solution/project, open new list if necessary
                        if (${'$'}currProj -ne ${'$'}warning.Project -or ${'$'}currSln -ne ${'$'}warning.Solution)
                        {
                            if (${'$'}currProj -ne "")
                            {
                                ${'$'}stream.WriteLine("          </ul>")
                                ${'$'}stream.WriteLine("        </li>")
                                ${'$'}currProj = ""
                            }
                            if (${'$'}currSln -ne ${'$'}warning.Solution)
                            {
                                if (${'$'}currSln -ne "")
                                {
                                    ${'$'}stream.WriteLine("      </ul>")
                                    ${'$'}stream.WriteLine("    </li>")
                                }
                                ${'$'}stream.WriteLine("    <li class='sln collapsed'>")
                                ${'$'}stream.WriteLine("      <label data-value=`"${'$'}(${'$'}warning.Solution)`">${'$'}(${'$'}warning.Solution)</label>")
                                ${'$'}stream.WriteLine("      <ul>")
                                ${'$'}currSln = ${'$'}warning.Solution
                            }
                            if (${'$'}warning.Project -ne "")
                            {
                                ${'$'}stream.WriteLine("        <li class='proj collapsed'>")
                                ${'$'}stream.WriteLine("          <label data-value=`"${'$'}(${'$'}warning.Project)`">${'$'}(${'$'}warning.Project)</label>")
                                ${'$'}stream.WriteLine("          <ul>")
                                ${'$'}currProj = ${'$'}warning.Project
                            }
                        }
                    
                        # Write list item
                        if (${'$'}warning.WarningMessage -ne "")
                        {
                            ${'$'}stream.WriteLine("            <li class='warning ${'$'}(if (${'$'}warning.IsNew) { "new" } else { "old" })' data-code='${'$'}(${'$'}warning.WarningCode)'>${'$'}([System.Web.HttpUtility]::HtmlEncode(${'$'}warning.WarningMessage))</li>")
                        }
                    }
                    ${'$'}stream.WriteLine("
                              </ul>
                            </li>
                          </ul>
                        </li>
                      </ul>
                    
                      <div>${'$'}(Get-Date -format g)</div>
                    
                    </body>
                    </html>")
                    ${'$'}stream.Close()
                    
                    # Publish as artifact
                    Write-Host ("##teamcity[publishArtifacts 'BuildWarningReport.html => .teamcity/BuildWarningReport.zip']")
                    
                    
                    
                    Write-Host
                    Write-Host "Complete"
                """.trimIndent()
            }
        }
    }
})
