## 1. Background & The Issue
My college network uses an enterprise-grade firewall with Deep Packet Inspection (DPI) to actively monitor and block certain websites. 

To bypass this, I installed **Zapret** (a DPI circumvention tool) and changed my system DNS to Cloudflare. However, several sites remained blocked. The main reasons for this initial failure were:
1. **DNS Hijacking:** The college firewall was intercepting standard DNS requests (Port 53), rendering the basic Cloudflare DNS change ineffective.
2. **Default DPI Settings:** Every enterprise firewall is configured differently. Zapret's default "fake packet" strategy was easily recognized and dropped by the college's firewall.
3. **QUIC Protocol Interference:** Modern browsers default to QUIC (HTTP/3 over UDP), but Zapret's primary bypassing rules apply to TCP connections.

## 2. Step-by-Step Resolution

### Step 1: Prevent DNS Hijacking (Enable DoH)
To stop the college network from intercepting and rerouting DNS queries, I enabled **DNS over HTTPS (DoH)** in the browser.
* **Action:** Went to Browser Settings > Privacy & Security > Secure DNS.
* **Setting:** Enabled "Use secure DNS" and explicitly selected **Cloudflare**.

### Step 2: Disable the QUIC Protocol
To ensure traffic travels over standard TCP (which Zapret manipulates), I forced the browser to disable HTTP/3.
* **Action:** Opened `chrome://flags` (or equivalent depending on the browser).
* **Setting:** Searched for **Experimental QUIC protocol** and set it to **Disabled**.

### Step 3: Analyze the Network (Blockcheck)
I needed to find exactly which packet manipulation techniques could trick the college's specific firewall.
* **Action:** Stopped the Zapret service and ran its built-in analyzer against a blocked domain (`net52.cc`).
```bash
sudo systemctl stop zapret
cd /opt/zapret
sudo ./blockcheck.sh
```
* **Result:** The analyzer tested dozens of methods and revealed that network-level packet manipulation (`nfqws`) using the `fakedsplit` strategy successfully bypassed the block.

### Step 4: Apply the Custom Configuration
With the working strategy identified, I applied it to the main Zapret configuration file.
* **Action:** Opened `/opt/zapret/config` using a text editor.
```bash
sudo nano /opt/zapret/config
```
* **Changes Made:** 
  * Updated the `NFQWS_OPT` variable to inject the working strategy while retaining the `<HOSTLIST>` flag:
    ```text
    NFQWS_OPT="--dpi-desync=fakedsplit --dpi-desync-ttl=2 --dpi-desync-split-pos=1 <HOSTLIST>"
    ```
  * Changed the filtering mode to apply rules globally for testing instead of waiting for a connection drop:
    ```text
    MODE_FILTER=none
    ```

### Step 5: Restart and Verify
Finally, I saved the configuration file and restarted the service to apply the customized parameters.
```bash
sudo systemctl restart zapret
sudo systemctl status zapret
```
* **Conclusion:** With DoH active, QUIC disabled, and the customized `fakedsplit` desync strategy applied via Zapret, all previously blocked websites successfully loaded without triggering the firewall or incurring a speed penalty.