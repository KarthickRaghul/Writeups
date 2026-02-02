# Challenge: Good Advice

**Category:** Forensics / Audio  
**Goal:** Recover the hidden flag

To Download the question file : https://raw.githubusercontent.com/KarthickRaghul/Writeups/main/Ctfs/DigitalCyberHunt/Resources/good-advice.zip

There was a Audio File named Unknown with no extension and not a proper audio file that couldn't be opened with any application .
- The output showed `RIFF` / `WAVE` strings
- This indicated the file was **meant to be a WAV audio file**

So I renamed the file into audio.wav and then tried opening  

![Pasted image 20260202132921](Pasted%20image%2020260202132921.png)  


After that I tried converting it to spectrogram , then 

```bash
> sox audio.wav -n spectrogram

sox FAIL formats: can't open input file `audio.wav': RIFF header not found
```

### Found the problem :

After Hex Analysis 

```bash
❯ xxd -g1 -l 64 audio.wav

00000000: 11 49 46 46 46 d0 02 00 57 41 56 45 66 6d 74 20  .IFFF...WAVEfmt 
00000010: 10 00 00 00 01 00 01 00 22 56 00 00 44 ac 00 00  ........V..D...
00000020: 02 00 10 00 4c 49 53 54 1a 00 00 00 49 4e 46 4f  ....LIST....INFO
00000030: 49 53 46 54 0d 00 00 00 4c 61 76 66 36 31 2e 31  ISFT....Lavf61.1
```

Expected WAV header : `52 49 46 46  ("RIFF")`

**Problem:**

1. Extra byte (`0x11`) at the start
2. "RIFF" was shifted and malformed

### Fixing the problem:

```bash
❯ dd if=audio.wav of=fixed.wav bs=1 skip=1

184397+0 records in
184397+0 records out
184397 bytes (184 kB, 180 KiB) copied, 0.508662 s, 363 kB/s

❯ printf 'RIFF' | dd of=fixed.wav bs=1 seek=0 count=4 conv=notrunc

4+0 records in
4+0 records out
4 bytes copied, 0.000113422 s, 35.3 kB/s

❯ file fixed.wav

fixed.wav: RIFF (little-endian) data
```

### Rebuilding it properly with sox:

```bash

❯ dd if=fixed.wav of=raw.pcm bs=1 skip=77
184320+0 records in
184320+0 records out
184320 bytes (184 kB, 180 KiB) copied, 0.48287 s, 382 kB/s

❯ sox -t raw -r 44100 -c 1 -b 16 -e signed-integer raw.pcm rebuilt.wav

❯ file rebuilt.wav

rebuilt.wav: RIFF (little-endian) data, WAVE audio, Microsoft PCM, 16 bit, mono 44100 Hz

❯ sox rebuilt.wav -d

rebuilt.wav:

 File Size: 184k      Bit Rate: 706k
  Encoding: Signed PCM    
  Channels: 1 @ 16-bit   
Samplerate: 44100Hz      
Replaygain: off         
  Duration: 00:00:02.09  

In:100%  00:00:02.09 [00:00:00.00] Out:92.2k [      |      ] Hd:4.5 Clip:0    
Done.
```

Now there will be three files created :
- rebuilt.wav
- fixed.wav 
- raw.pcm
### Finding the Flag:

Now opened the rebuilt.wav in Audacity and slow the audio and listen to it :

![Pasted image 20260202134830](Pasted%20image%2020260202134830.png)

We can find it saying a1w4ysth3r34u

By playing it multiple times and understanding that the clue in the question is that 
 `God is always there for you, no matter your circumstances.` Therefore Came to a conclsion that this is the flag.

## Flag :

Found out the final flag to be :

```
ThunderCipher{a1w4ysth3r34u}
```
