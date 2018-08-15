# Initialization and Elimination 
def __init__
def __del__

# Random options
def get_random
def zymkey.module.Zymkey.get_random ( self, 
	num_bytes 
	)

def create_random_file
def zymkey.module.Zymkey.create_random_file ( self, 
	file_path, 
	num_bytes 
	)

# Encrypt & Decrypt
def lock
def zymkey.module.Zymkey.lock ( self, 
	src, 
	dst = None, 
	encryption_key = ZYMKEY_ENCRYPTION_KEY 
	)

def unlock
def zymkey.module.Zymkey.unlock ( self, 
	src, 
	dst = None, 
	encryption_key = ZYMKEY_ENCRYPTION_KEY, 
	raise_exception = True 
	)

# Signature --> Generation
def sign
def zymkey.module.Zymkey.sign ( self, 
	src, 
	slot = 0 
	)

def sign_digest
def zymkey.module.Zymkey.sign_digest ( self, 
	sha256, 
	slot = 0 
	)

# Signature --> Verification
def verify
def zymkey.module.Zymkey.verify ( self, 
	src, 
	sig, 
	raise_exception = True, 
	slot = 0, 
	pubkey = None, 
	pubkey_curve = ’NISTP256’, 
	sig_is_der = False 
	)

def verify_digest
def zymkey.module.Zymkey.verify_digest ( self, 
	sha256, 
	sig, 
	raise_exception = True, 
	slot = 0, 
	pubkey = None, 
	pubkey_curve = ’NISTP256’, 
	sig_is_der = False 
	)

# ECDSA options
def create_ecdsa_public_key_file
def zymkey.module.Zymkey.create_ecdsa_public_key_file ( self, 
	filename, 
	slot = 0 
	)

def get_ecdsa_public_key
def zymkey.module.Zymkey.get_ecdsa_public_key ( self, 
	slot = 0 
	)


# Taking Master's roll in I2C
def set_i2c_address
def zymkey.module.Zymkey.set_i2c_address ( self, 
	address 
	)

# RTC
def get_time
def zymkey.module.Zymkey.get_time ( self, 
	precise = False 
	)


# TAP
def set_tap_sensitivity
def zymkey.module.Zymkey.set_tap_sensitivity ( self, 
	axis = ’all’, 
	pct = 50.0 
	)

def wait_for_tap
def zymkey.module.Zymkey.wait_for_tap ( self, 
	timeout_ms = -1 
	)

def get_accelerometer_data
def zymkey.module.Zymkey.get_accelerometer_data ( self )


# Perimeter
def wait_for_perimeter_event
def zymkey.module.Zymkey.wait_for_perimeter_event ( self, 
	timeout_ms = -1 
	)

def set_perimeter_event_actions
def zymkey.module.Zymkey.set_perimeter_event_actions ( self, 
	channel, 
	action_notify = True, 
	action_self_destruct = False 
	)

def get_perimeter_detect_info
def zymkey.module.Zymkey.get_perimeter_detect_info ( self )

def clear_perimeter_detect_info
def zymkey.module.Zymkey.clear_perimeter_detect_info ( self )


#LED Control
def led_on
def led_off
def led_flash
def zymkey.module.Zymkey.led_flash( self, 
	on_ms, 
	off_ms=0, 
	num_flashes=0 
	)

dsadfa 