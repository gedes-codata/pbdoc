function cpfMask(v) {
	if (v && v.length > 14) {
		v = v.substring(0, 14)
	}
	v = v.replace(/\D/g, "");
	v = v.replace(/(\d{3})(\d)/, "$1.$2");
	v = v.replace(/(\d{3})(\d)/, "$1.$2");
	v = v.replace(/(\d{3})(\d{1,2})$/, "$1-$2");
	return v;
}
