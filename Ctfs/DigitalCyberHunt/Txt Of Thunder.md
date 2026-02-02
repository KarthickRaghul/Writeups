
 **Challenge**: TXT OF THUNDER  
**Category**: Web  
**Goal**: Find the hidden flag

## Initial Reconnaissance

Opening the website showed a clean corporate landing page for Thunder Solutions.
At first glance, nothing suspicious or flag-like was visible.

Basic checks performed:

Page content inspection

HTML source review

HTTP headers inspection

No flag was directly visible in the page or headers.

## Source Code Analysis

Viewing the page source (View Source) revealed an interesting HTML comment:

![[Writeups/Ctfs/DigitalCyberHunt/Resources/image.png|Page Source View]]

<!-- Nothing interesting here. Bots read rules, humans read pages. -->


This comment strongly hinted at robots.txt, since:

“Bots read rules” → robots.txt



“Humans read pages” → visit paths manually

## Inspecting robots.txt

Navigating to:

http://text.thundercipher.tech/robots.txt

![[image-1.png|Robots.txt]]


Revealed the following content:

At the very end of the file, outside of comments, an extra line appeared:

/flagisinhtmlfilenottxtfile.txt


This was a clear intentional leak.

## Decoy Detection

Attempting to access:

http://text.thundercipher.tech/flagisinhtmlfilenottxtfile.txt


Resulted in 404 Not Found.

The filename itself was a deliberate misdirection, literally saying:

flag is in html file not txt file

This indicated:

The .txt file was fake

The real flag was inside an HTML file

Likely located inside the disallowed directory

## Discovering the Hidden Directory

From robots.txt, the directory:

/txt-of-thunder/


was explicitly disallowed.

Opening it manually:

http://text.thundercipher.tech/txt-of-thunder/


Returned an HTML page instead of a directory error.

![[image-2.png|Final Attempt]]

## Inspecting the Hidden Page

Viewing the page source revealed:

<!-- Flag -->
<p style="display:none;">ThunderCipher{txt_of_thunder_7681912}</p>


The flag was hidden using CSS (display:none), making it invisible on normal viewing but easily discoverable through source inspection.

## Final Flag

```
ThunderCipher{txt_of_thunder_7681912}
```