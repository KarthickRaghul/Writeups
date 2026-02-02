# Challenge: Panel 290

**Category**: Hardware
**Goal**: Recover the hidden flag from PCB Gerber files

---

## Challenge Description

**Panel - The innovation that redefines the future of electronics**

A mysterious PCB design file archive was provided containing multiple Gerber layers. The challenge required extracting and analyzing these hardware design files to find the hidden flag.

To Download the the question file :  https://raw.githubusercontent.com/KarthickRaghul/Writeups/main/Ctfs/panel290.zip]

---

## Initial Analysis

### Step 1: Extract and Identify Files

The provided `File.zip` contained multiple Gerber files:
- `Gerber_TopLayer.GTL` (top copper layer)
- `Gerber_BottomLayer.GBL` (bottom copper layer)
- `Gerber_InnerLayer4.G4` (inner layer 4)
- `Gerber_TopSilkscreenLayer.GTO` (silkscreen)
- `Gerber_TopSolderMaskLayer.GTS` (solder mask)
- `Gerber_BottomSolderMaskLayer.GBS` (bottom solder mask)
- `Gerber_TopPasteMaskLayer.GTP` (paste mask)
- `Gerber_BoardOutlineLayer.GKO` (board outline)
- `Gerber_DocumentLayer.GDL` (document layer)
- Various drill files

**Problem Identified:** The inner layer contained information, but it appeared as mirrored text - like viewing the PCB from the opposite side through water reflection.

---

## Discovery: Mirrored Text on Inner Layer

### Step 2: Render Gerber Layers

Created Python script `decode_inner_layer.py` to parse and visualize Gerber coordinates:

```python
import re
import matplotlib.pyplot as plt
import numpy as np

# Parse Gerber inner layer file
with open('Gerber_InnerLayer4.G4', 'r') as f:
    content = f.read()

# Extract X,Y coordinates from aperture commands
pattern = r'D\d+\*\n(.*?)(?=D\d+|\%)'
coordinates = []

for line in content.split('\n'):
    if 'X' in line and 'Y' in line:
        match = re.findall(r'X(-?\d+)Y(-?\d+)', line)
        if match:
            for x, y in match:
                coordinates.append((int(x), int(y)))

# Visualize
if coordinates:
    xs, ys = zip(*coordinates)
    plt.figure(figsize=(16, 10))
    plt.scatter(xs, ys, s=1, alpha=0.5)
    plt.title('Inner Layer 4 - Gerber Visualization')
    plt.savefig('inner_layer_high_contrast.png', dpi=150, bbox_inches='tight')
    plt.show()
```

**Result:** The visualization revealed text on the inner layer, but it was **horizontally mirrored** - text read backward from right to left.

```
Visual Output (mirrored):
 ⇐ W3iV_br3G{ ]rehpiC r^enduhT
```

---

## Step 3: Flip the Mirrored Image

Created `flip_image.py` to horizontally mirror the image:

```python
from PIL import Image

# Load the rendered inner layer image
img = Image.open('inner_layer_high_contrast.png')

# Flip horizontally to reverse the mirroring
flipped_img = img.transpose(Image.FLIP_LEFT_RIGHT)

# Save the corrected image
flipped_img.save('inner_layer_FLIPPED.png')
```

**Result:** After flipping, the text became readable:

```
ThunderCipher{!G3rb_V13w^!;
```

---

## Step 4: Decode Base32 Encoding

The text appeared to be Base32 encoded. Extracted the encoded portion and attempted decoding:

```python
import base64

# The encoded portion from the mirrored text
encrypted = "KRUHK3TEMVZEG2LQNBSXE6ZBI4ZXEYS7KYYTG526EE5X"

# Base32 decode with proper padding
# Base32 requires padding to multiple of 8 characters
padding_needed = (8 - len(encrypted) % 8) % 8
padded = encrypted + '=' * 4  # Empirically found 4 padding chars worked

try:
    decoded = base64.b32decode(padded)
    result = decoded.decode('utf-8')
    print(f"Decoded: {result}")
except Exception as e:
    print(f"Error: {e}")
```

**Result:**
```
Base32 Decoded: ThunderCipher{!G3rb_V13w^!;
```

---

## Step 5: Extract the Final Flag

The decoded Base32 text directly revealed the flag with special characters intact.

**Decoded Output:**
```
ThunderCipher{!G3rb_V13w^!;}
```

**Result:** The flag format accepts special characters (`!`, `^`, `;`) as valid characters within the cipher payload.

---

## Final Flag

```
ThunderCipher{!G3rb_V13w^!;}
```
