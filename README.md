# Yocto for Raspberry Pi Zero W (MIDI synthesizer)

A Yocto (`scarthgap`) image for the Raspberry Pi Zero W. The long-term goal is
a simple synthesizer controlled from a USB MIDI keyboard (fluidsynth). For now
the image provides: a booting system, WiFi that automatically connects to
saved networks, SSH (dropbear) and a serial console.

## What is in this repo

The repo contains only the `meta-local` layer and `kas.yml`, which describes
where to fetch the rest (poky, meta-openembedded, meta-raspberrypi) and how to
configure the build. The upstream layers are not stored here.

```
meta-local/
├── conf/layer.conf
├── conf/machine/raspberrypi0-wifi-synth.conf   machine: UART, USB host (dwc2)
├── recipes-core/images/rpi0-synth-image.bb     image (core-image-minimal + kernel modules + WiFi + SSH)
├── recipes-connectivity/wpa-supplicant/        WiFi network configuration
├── recipes-core/init-ifupdown/                 wlan0 in /etc/network/interfaces
├── recipes-bsp/bootfiles/                      rpi-bootfiles fetch fix (checksum)
└── recipes-kernel/linux/                       shallow git clone of the kernel
```

The custom machine `raspberrypi0-wifi-synth` inherits from `raspberrypi0-wifi`
(meta-raspberrypi) and adds `raspberrypi0-wifi` to `MACHINEOVERRIDES`, so the
upstream overrides (e.g. the kernel defconfig) keep applying to it.

## Hardware

- Raspberry Pi Zero W v1.1
- Serial console: a USB-TTL adapter wired directly to the GPIO header
  (pin 6 = GND, 8 = TXD, 10 = RXD), 115200 baud. Bluetooth is disabled
  (`disable-bt`) so the full UART is on GPIO14/15.
- Waveshare USB HUB HAT on the GPIO header, connected with a
  microUSB-to-microUSB cable to the board's OTG port. The port runs in host
  mode (`dwc2,dr_mode=host`).
- Power through the "PWR IN" port.

## Building

1. Install the host dependencies (Debian; list from the Yocto documentation):

   ```bash
   sudo apt install gawk wget git diffstat unzip texinfo gcc build-essential \
       chrpath socat cpio python3 python3-pip python3-pexpect xz-utils \
       debianutils iputils-ping python3-git python3-jinja2 libegl1 \
       libsdl2-dev python3-subunit mesa-common-dev zstd lz4 file locales \
       libacl1 pipx
   pipx install kas
   ```

2. Configure WiFi. The file with the PSK hashes is not in the repo
   (it is in `.gitignore`), create it from the template:

   ```bash
   cd meta-local/recipes-connectivity/wpa-supplicant/wpa-supplicant
   cp wpa_supplicant.conf-sane.example wpa_supplicant.conf-sane
   wpa_passphrase "SSID" "PASSWORD" | grep -v '#psk'   # paste the result into the file
   ```

   Add one `network={}` block per network. `priority` decides which network
   is preferred when several are in range (higher wins). A phone hotspot must
   use WPA2 on 2.4 GHz, the Zero W has no 5 GHz radio. Never commit the file.

3. Build:

   ```bash
   kas build kas.yml
   ```

   The first build takes several hours. The image ends up in
   `build/tmp/deploy/images/raspberrypi0-wifi-synth/`. To reuse existing
   downloads and sstate cache from another build directory, export
   `DL_DIR` and `SSTATE_DIR` before running `kas`.

`kas` creates `build/`, generates `conf/local.conf` and `conf/bblayers.conf`
from `kas.yml` and loads the build environment (the equivalent of
`source oe-init-build-env`). Do not edit `build/conf/local.conf` by hand, kas
overwrites it. Put persistent settings in `kas.yml` (`local_conf_header`) or
in the layer.

For manual BitBake commands (`bitbake -e`, `bitbake-layers`, ...) open a
shell with the environment loaded:

```bash
kas shell kas.yml
```

### Pinning layer versions

`kas.yml` follows the `scarthgap` branch of each upstream layer, so two builds
made at different times can differ (kas warns about this). To record the exact
commits, generate a lock file and commit it next to `kas.yml`:

```bash
kas lock kas.yml      # writes kas.lock.yml, picked up automatically by kas
kas lock --update kas.yml   # later: move to newer upstream commits
```

## Flashing and first boot

```bash
lsblk    # find the SD card device, do not pick your system disk
sudo bmaptool copy \
    build/tmp/deploy/images/raspberrypi0-wifi-synth/rpi0-synth-image-raspberrypi0-wifi-synth.rootfs.wic.bz2 \
    /dev/sdX
```

Insert the card, connect power and open the serial console:
`screen /dev/ttyUSB0 115200` (login `root`, no password). Once the board has
joined WiFi, find its IP address on the router or with `ip a show wlan0`
and log in with `ssh root@<IP>`.

Passwordless `root` is a development setting (`debug-tweaks` in `kas.yml`),
do not leave it on a device exposed to a network.

### Changing WiFi networks on a running board

`update_config=1` is enabled, so networks can be added over SSH or UART
without rebuilding:

```bash
wpa_passphrase "SSID" "PASSWORD" | grep -v '#psk' >> /etc/wpa_supplicant.conf
wpa_cli -i wlan0 reconfigure
```

Changes survive reboots but are lost when a new image is flashed. Networks
that should always be present belong in `wpa_supplicant.conf-sane` in the
layer.

## License

MIT, see `LICENSE`. The license covers the files in this repository only;
the built image contains software under its own licenses.
