# Coolstream HD2 BSP default settings
IMAGE_CLASSES += "usbimg"
JFFS2_ERASEBLOCK = "${@'0x20000' if BOXMODEL != 'tank' else '0x40000'}"
JFFS2_SUM_EXTRA_ARGS = "-e ${JFFS2_ERASEBLOCK} -n -l"
EXTRA_IMAGECMD_jffs2 = "-e ${JFFS2_ERASEBLOCK} -n -l -U --cleanmarker=16"
