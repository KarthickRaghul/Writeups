## Self-Hosted Homelab
##### Ubuntu Server + Docker + Portainer + Tailscale

---

**Current Architechture** :


                   Internet
                      │
                      │
              Tailscale Mesh VPN
                      │
      ┌───────────────┴───────────────┐
      │                               │
 Ubuntu Server                  Laptop / Phone
 100.\*\*\*.\*\*\*.\*\*\*                100.xx.xx.xx
      │
      ├── SSH (22)
      ├── Nextcloud (8080)
      ├── Portainer (9443)
      └── Future AI Services

---
# Storage Layout

```
1 TB HDD
│
├── Ubuntu Server
│   ├── Root (100 GB)
│   └── Boot
│
└── Storage Partition (~800 GB)
    └── /storage
        ├── nextcloud
        ├── backups
        ├── websites
        └── ai
```


```

---

# Installed Software

- Ubuntu Server
- Docker
- Portainer
- Nextcloud
- MariaDB
- Tailscale

---

# Docker Containers

```text
nextcloud
nextcloud-db
portainer
```

Verify:

```bash
docker ps
```

---

# Portainer

Default Port

```
9443
```

Access

```
https://SERVER-IP:9443
```

Example

```
https://192.168.29.202:9443
```

---

# Nextcloud

Container Port

```
8080
```

Access

```
http://SERVER-IP:8080
```

Example

```
http://192.168.29.202:8080
```

---

# Install Tailscale (Ubuntu Server)

Install

```bash
curl -fsSL https://tailscale.com/install.sh | sh
```

Enable service

```bash
sudo systemctl enable --now tailscaled
```

Login

```bash
sudo tailscale up
```

A browser URL will be displayed.

Example

```
https://login.tailscale.com/a/XXXXXXXX
```

Authenticate using your Tailscale account.

---

# Verify Tailscale

Check status

```bash
tailscale status
```

Example

```text
100.71.241.102  karthihome
```

Current server Tailscale IP

```
100.71.241.102
```

Check IP

```bash
tailscale ip -4
```

---

# Install Tailscale (Laptop)

## Arch Linux

Install

```bash
sudo pacman -S tailscale
```

Enable daemon

```bash
sudo systemctl enable --now tailscaled
```

Login

```bash
sudo tailscale up
```

Authenticate using the **same account** as the server.

---

# Verify Laptop

```bash
tailscale status
```

Expected

```text
100.xx.xx.xx     laptop
100.71.241.102   karthihome
```

---

# Test Connection

Ping

```bash
ping 100.71.241.102
```

or

```bash
tailscale ping 100.71.241.102
```

SSH

```bash
ssh karthi@100.71.241.102
```

---

# Access Services Through Tailscale

## SSH

```bash
ssh karthi@100.71.241.102
```

## Nextcloud

```
http://100.71.241.102:8080
```

## Portainer

```
https://100.71.241.102:9443
```

---

# Useful Tailscale Commands

Status

```bash
tailscale status
```

Current IP

```bash
tailscale ip -4
```

Restart

```bash
sudo systemctl restart tailscaled
```

Reconnect

```bash
sudo tailscale up
```

Logout

```bash
sudo tailscale logout
```

Daemon Status

```bash
systemctl status tailscaled
```

---

# Troubleshooting

## Cannot connect to daemon

```
Failed to connect to local Tailscale daemon
```

Start daemon

```bash
sudo systemctl enable --now tailscaled
```

---

## Check daemon

```bash
systemctl status tailscaled
```

---

## Check login

```bash
tailscale status
```

---

## Verify server IP

```bash
tailscale ip -4
```

---
