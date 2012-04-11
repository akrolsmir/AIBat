This program assists mappers and modders by automating many of the tedious, clerical checks that otherwise would have been done by hand.

Its use is designed to be intuitive, but if you have any questions drop by http://osu.ppy.sh/forum/t/55305.

Enjoy!

Changelog:

Version 1.1- 3/27/12
- Check for preview point not set (credit: Garven)
- Updated SkinSpecs as per FAQ (credit: Gens)
- Updated warnings export to clipboard (as ziin suggested ages ago)
- Slight optimizations
- Suggest snap times for timing points
- Check that spinners end properly (credit: ziin)

Version 1.0 - 12/10/11 (I should have been studying for finals T_T)
- Added opened folder name to the title of the AIBat window, for reference.
- Vastly improved search- switched to instantaneous search, unified into 1 tab
- Bugfix: Snap checking failed when offset is a decimal (credit: Breeze)
- Added reversearrow.png (credit: Sakura Hana)
- Bugfix: Not opening when settings.txt is missing
- Implemented bug report in case of error
- Bugfix: Not working on certain maps with decimal beatspacing (credit: Miya)
- Deal with SB and skin animations properly (credit: Sakura Hana, ziin, Bliss)
- Cleaned up code somewhat

Version 0.7b - 8/17/11
- Added "Export Hitsounds to Bookmarks" feature (credit: ziin)
- Recommend against opening osu song folder.
- Added refresh feature, warning before exit, check for Kiai starting on downbeat (credit: Sakura Hana)
- Added "display folder" feature
- Added mp3 check, added custom hitsound check

Version 0.6b - 7/4/11 (Independence Day!)
- Bugfix: .png check, epilepsy check not working properly (credit: BlissfulYoshi)
- Optimized Hitobjects by using String.split instead of Scanner.
- Add option to show changelog.
- Added bookmarks feature
- Added search option
- Slightly reformatted headers and removed "no problems"
- Displays mp3 bitrate - will be changed to a check next release.

Version 0.5b - 6/26/11
- Code cleaned up
- Bugfix: Fixed "Missing SB Files" not working properly.
- Suggest epilepsy warnings for non-empty storyboards lacking them.
- Checks for storyboard elements that aren't .png files.
- Copy note references (e.g. "00:12:345 (6)") to clipboard on click.
- Checks for "other" skinfiles, included more specifications for skin files.


Version 0.4b - 6/24/11
- Improved speed (what do you mean, String.concat() isn't faster than Stringbuilder?)
- Checks for break length between 750 and 15000, suggests break after 1:30, warns after 2:15
- Checks for consistency on Kiai times across all difficulties
- Checks for all skin elements present in a set, and 128x128 for the ones in Hitcircle.
- Checks for BG size = 1024x768, or missing BG
- Checks for codeless elements, missing elements, unused images, multiple osbs

Version 0.3b - 6/21/11
- Changed max file size with video to 24 mb
- Removed check for stack leniency consistency, instead checks for leniency < 0.3
- Checks for inconsistency in red timing sections, combo colours
- Checks for snapping in inherited points
- Added ability to export all text formatted for forum posting to clipboard
- Added keyboard shortcuts Ctrl + O for open, Ctrl + Shift + C to export to clipboard.

Version 0.2b - 6/20/11
- Can press "Enter" key on keyboard instead of the enter button.
- Simplified messages to "No problems."
- Added tag suggestions for no tags or for missing guest names.
- Checks for filesize , max of 10mb (or 20 with video)
   -Recursive algorithm- can cause lag if you try to check the size of C:\ or sth.
- Checks for 750 ms < spinner length < 7500 ms.
- Checks for Catmull sliders

Version 0.1b - 6/18/11
- GUI mostly complete
- Checks for snapping at start and end of hitobjects
- Checks that data under [General] and [Metadata] are consistent on all diffs

********************

Priorities:
*- clean up AIBatTabs, seperate into different tabs, array in the relevant classes.
*- tooltip instead of popup on click
*- output difficulty values (Breeze)
- improve settings, autogenerate settings
- Settings menu- Permanently disable popup, hide unused warnings
- Update the output to match 
- Search button in panel
- reduce cross-dependency from dialogs



To do:
- Ask peppy for HitObject for slider's endposition;
	- Add option to show all distance spacings (from OsuDistanceChecker).
- Check for circle size between 3-7
- check if audioleadin total less than 2 secs
- Order tabs by number of hitobjects
- Add option to close individual tabs
- Add option to save all warnings as txt.
- Add Label, reSearch and Enter options in Searcher.
- Encourage compatibility with system.newline and character separator
- Standardize .osu files on click.

Implement many more checks:
- MP3 bitrate
- # diffs < 6?
- warning for white color + kiai
- warning for same color
- All hitsounds must be > 42 bytes
- More than 3 slider velocities

Storyboarding:
Get all other SB file specifications.

********************

To fix:
Breaking upon a decimal x-value, y-value, slider length, times.
Scroll bar dropping to the bottom

Code cleanup:
check vs. get
Enter keystroke
optimize w/ Stringbuilders
investigate: first open is slowest
clean up warn[], remove check[]
merge warning and getAllWarnings;
cutPath or not
use threads

********************
AiMod Only:
-Not always on top
-Tabbed like AIBat
-Remove Distance, Difficulty
-Hyperlink -> clipboard