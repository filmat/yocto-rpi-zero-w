require recipes-core/images/core-image-minimal.bb

IMAGE_INSTALL:append = " kernel-modules wpa-supplicant wpa-supplicant-passphrase linux-firmware-rpidistro-bcm43430 usbutils alsa-utils fluidsynth-bin timgm6mb-soundfont"
IMAGE_FEATURES += "ssh-server-dropbear"
