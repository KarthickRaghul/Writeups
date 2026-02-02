#  CTF Writeup — Easy Cipher

##  Challenge Information

- **Challenge Name:** Easy Cipher  
- **Category:** Crypto  
- **Difficulty:** Easy  
- **Hint Provided:**  
  > "So eassssssy!"  

- **Cipher Text Given:**  
VGh1bmRlckNpcGhlcnszNHN5X2I0czN9


---

##  Challenge Description

This is a beginner-level cryptography challenge.  
The hint *"So eassssssy!"* suggests that the cipher is extremely common and simple to decode.

The provided text appears to be an encoded string rather than an encrypted one.

---

## 🔍 Solution Walkthrough

###  Step 1: Identify the Encoding

The cipher text:

VGh1bmRlckNpcGhlcnszNHN5X2I0czN9


ends with `=`-like padding structure and contains only valid Base64 characters.

This strongly indicates that the message is encoded using **Base64**.

---

###  Step 2: Decode Using Terminal Command

We can decode the Base64 string easily using the Linux terminal:

```bash
echo "VGh1bmRlckNpcGhlcnszNHN5X2I0czN9" | base64 --decode


Final Flag :
   
     ThunderCipher{34sy_b4s3}

