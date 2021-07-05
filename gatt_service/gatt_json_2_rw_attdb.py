import json
import re


def str2value(uuid_str):
    if isinstance(uuid_str, int) or isinstance(uuid_str, bytes):
        return uuid_str
    uuid_str = uuid_str.strip()
    try:
        if uuid_str[0] == '{' or uuid_str[-1] == '}':
            uuid_str = uuid_str.replace('{', '').replace('}', '')
            return bytes(bytearray.fromhex(uuid_str))
        else:
            return int(uuid_str, 0)
    except TypeError:
        return 0


def dump_services(services_dict):
    for service in services_dict:
        assert isinstance(service, dict)
        serv_name = service.get('name', 'unknown')
        print('Service name:', serv_name)
        serv_type = service.get('type', 'unknown')
        print('Service type:', serv_type)
        serv_uuid = service.get('uuid', 'unknown')
        print('Service uuid:', serv_uuid)
        serv_comments = service.get('comments', '')
        if len(serv_comments) > 0:
            print('Service comments:', serv_comments)
        serv_characteristics = service.get('characteristics', None)
        # characteristics
        if isinstance(serv_characteristics, list):
            for characteristic in serv_characteristics:
                assert isinstance(characteristic, dict)
                char_name = characteristic.get('name', 'unknown')
                print('\tChar name:', char_name)
                char_uuid = characteristic.get('uuid', 'unknown')
                print('\tChar uuid:', char_uuid)
                char_properties = characteristic.get('properties', 'Unknown')
                print('\tChar properties:', char_properties)
                char_permission = characteristic.get('permission', None)
                if char_permission is None:
                    print('Char permission read/write: ', 'no_auth/no_auth')
                elif isinstance(char_permission, dict):
                    read_permission = characteristic.get('read', 'no_auth')
                    write_permission = characteristic.get('write', 'no_auth')
                    print('\tChar permission read/write: {}/{}'.format(read_permission, write_permission))
                char_max_length = characteristic.get('max_length', 'Unknown')
                print('\tChar max_length:', char_max_length, 'bits')
                # values
                char_values = characteristic.get('values', None)
                if isinstance(char_values, list) and len(char_values) > 0:
                    print('\tChar Values:')
                    for value in char_values:
                        assert isinstance(value, dict)
                        value_name = value.get('name', 'Unknown')
                        print('\t\tname:', '%-32s,' % value_name, end='')
                        value_bit_length = value.get('bit_length', 'Unknown')
                        print('\tlength: %-2d bits,' % value_bit_length, end='')
                        value_type = value.get('type', 'Unknown')
                        print('\ttype: %-10s' % value_type, end='')
                        value_default = value.get('default', '0')
                        print('\tdefault: %-5s,' % value_default, end='')
                        value_comments = value.get('comments', '')
                        if len(value_comments) > 0:
                            print('\t: %s' % value_comments, end='')
                        print('')
                # descriptors
                char_descriptors = characteristic.get('descriptors', None)
                if isinstance(char_descriptors, list) and len(char_descriptors) > 0:
                    for desc in char_descriptors:
                        assert isinstance(desc, dict)
                        desc_uuid = desc.get('uuid', 'Unknown')
                        print('\tDesc uuid:', desc_uuid, end=', ')
                        desc_permission = desc.get('permission', None)
                        if desc_permission is None:
                            print('permission read/write: ', 'no_auth/no_auth', end=', ')
                        elif isinstance(desc_permission, dict):
                            read_permission = desc.get('read', 'no_auth')
                            write_permission = desc.get('write', 'no_auth')
                            print('permission read/write: {}/{}'.format(read_permission, write_permission), end=', ')
                        desc_comments = desc.get('comments', '')
                        if len(desc_comments) > 0:
                            print(':', desc_comments, end='')
                        print('')

                char_comments = characteristic.get('comments', '')
                if len(char_comments) > 0:
                    print('Char comments:', char_comments)
                print('\t--------------------------')


class GattCharValue:
    def __init__(self, name, bit_length, value_type=0, default=0, comments=''):
        self.name = name
        self.bit_length = bit_length
        self.type = value_type
        self.default = str2value(default)
        self.comments = comments

    def value2str(self):
        if self.type == GattCharValue.TYPE_STREAM:
            if isinstance(self.default, bytes):
                return "'" + ' '.join(['%02X' % n for n in self.default]) + "'"
            else:
                return 'NULL'
        elif self.type == GattCharValue.TYPE_HEX:
            return '0x%02X' % self.default
        else:
            return '%d' % self.default

    def __str__(self):
        res = 'N: %-32s | L: %-2d bits | T:%-8s | D: %-5s | %s' % \
              (self.name, self.bit_length, self.type2str(self.type), self.value2str(), self.comments)
        return res

    @staticmethod
    def str2type(type_str):
        if type_str.lower() == 'unsigned':
            return GattCharValue.TYPE_UNSIGNED
        elif type_str.lower() == 'signed':
            return GattCharValue.TYPE_SIGNED
        elif type_str.lower() == 'hex':
            return GattCharValue.TYPE_HEX
        elif type_str.lower() == 'enum':
            return GattCharValue.TYPE_ENUM
        else:
            return GattCharValue.TYPE_STREAM

    @staticmethod
    def type2str(type_idx):
        if type_idx == GattCharValue.TYPE_UNSIGNED:
            return 'unsigned'
        elif type_idx == GattCharValue.TYPE_SIGNED:
            return 'signed'
        elif type_idx == GattCharValue.TYPE_HEX:
            return 'hex'
        elif type_idx == GattCharValue.TYPE_ENUM:
            return 'enum'
        else:
            return 'stream'
    TYPE_STREAM = 0
    TYPE_UNSIGNED = 1
    TYPE_SIGNED = 2
    TYPE_HEX = 3
    TYPE_ENUM = 4


class Attribute:
    def __init__(self, uuid, prop, max_length, read_perm=0, write_perm=0, comments=''):
        self.uuid = str2value(uuid)
        self.read_perm = read_perm
        self.write_perm = write_perm
        self.comments = comments
        self.prop = prop
        self.max_length = max_length

    @staticmethod
    def prop2str(prop):
        symbol = 'BRWCNISE'
        prop_exist = []
        for idx in range(len(symbol)):
            if (prop >> idx) & 1:
                prop_exist.append(symbol[idx])
        return '/'.join(prop_exist)

    @staticmethod
    def str2prop(prop_str):
        symbol_dict = {'B': 0, 'R': 1, 'W': 2, 'C': 3, 'N': 4, 'I': 5, 'S': 6, 'E': 7, }
        res = sum([(1 << n) for n in [symbol_dict.get(p, None) for p in prop_str.split('/')] if n is not None])
        return res

    @staticmethod
    def perm2str(perm):
        if perm == Attribute.PERM_SC:
            return 'sc'
        elif perm == Attribute.PERM_AUTH:
            return 'auth'
        elif perm == Attribute.PERM_AUTH:
            return 'un_auth'
        else:
            return 'no_auth'

    @staticmethod
    def str2perm(perm_str):
        if perm_str == 'sc':
            return Attribute.PERM_SC
        elif perm_str == 'auth':
            return Attribute.PERM_AUTH
        elif perm_str == 'un_auth':
            return Attribute.PERM_UN_AUTH
        else:
            return Attribute.PERM_NO_AUTH

    PROP_BIT_BROADCAST = 0
    PROP_BIT_READ = 1
    PROP_BIT_WRITE = 2
    PROP_BIT_WRITE_CMD = 3
    PROP_BIT_NOTIFY = 4
    PROP_BIT_INDICATE = 5
    PROP_BIT_SIGNED_WRITE = 6
    PROP_BIT_EXTEND = 7
    PERM_NO_AUTH = 0
    PERM_UN_AUTH = 1
    PERM_AUTH = 2
    PERM_SC = 3


class GattDescriptor(Attribute):
    def __init__(self, uuid, read_perm=0, write_perm=0, comments='',
                 prop=(1 << Attribute.PROP_BIT_READ) | (1 << Attribute.PROP_BIT_WRITE), max_length=2):
        Attribute.__init__(self, uuid, prop, max_length, read_perm, write_perm, comments)

    def __str__(self):
        res = 'Desc: 0x%04X, permission read/write: %s/%s, %s' % \
              (self.uuid, self.perm2str(self.read_perm), self.perm2str(self.write_perm), self.comments)
        return res

    @staticmethod
    def prop2str(prop):
        symbol = '_RW_____'
        prop_exist = []
        for idx in range(len(symbol)):
            if (prop >> idx) & 1:
                prop_exist.append(symbol[idx])
        return '/'.join(prop_exist)

    @staticmethod
    def perm2str(perm):
        if perm == GattDescriptor.PERM_SC:
            return 'sc'
        elif perm == GattDescriptor.PERM_AUTH:
            return 'auth'
        elif perm == GattDescriptor.PERM_AUTH:
            return 'un_auth'
        else:
            return 'no_auth'

    @staticmethod
    def str2perm(perm_str):
        if perm_str == 'sc':
            return GattDescriptor.PERM_SC
        elif perm_str == 'auth':
            return GattDescriptor.PERM_AUTH
        elif perm_str == 'un_auth':
            return GattDescriptor.PERM_UN_AUTH
        else:
            return GattDescriptor.PERM_NO_AUTH


class GattCharacteristic(Attribute):
    def __init__(self, name, uuid, prop, max_length, read_perm=0, write_perm=0, comments=''):
        Attribute.__init__(self, uuid, prop, max_length, read_perm, write_perm, comments)
        self.name = name
        self.values = []
        self.descriptors = []

    def add_value(self, name, bit_length, value_type=0, default=0, comments=''):
        self.values.append(GattCharValue(name, bit_length, value_type, default, comments))

    def add_descriptor(self, uuid, prop, max_length, read_perm=0, write_perm=0, comments=''):
        self.descriptors.append(GattDescriptor(uuid, read_perm, write_perm, comments))

    def __str__(self):
        str_list = [
            'Char name: %s' % self.name,
            'Char uuid: 0x%04X' % self.uuid,
            'Char properties: %s' % self.prop2str(self.prop),
            'Char permission read/write: %s/%s' % (self.perm2str(self.read_perm), self.perm2str(self.write_perm)),
            'Char max_length: %d bits' % self.max_length,
        ]
        if len(self.values) > 0:
            str_list.append('Char Values:')
            for value in self.values:
                str_list.append('\t' + str(value))
        for descriptor in self.descriptors:
            str_list.append(str(descriptor))
        if len(self.comments) > 0:
            str_list.append('Char comment: %s' % self.comments)
        return '\n'.join(('\t' + s) for s in str_list) + '\n\t--------------------------'


class GattService:
    def __init__(self, name, serv_type, uuid, comments=''):
        self.name = name
        self.type = str2value(serv_type)
        self.uuid = str2value(uuid)
        self.comments = comments
        self.characteristics = []

    def add_characteristic(self, name, uuid, prop, max_length, read_perm=0, write_perm=0, comments=''):
        char = GattCharacteristic(name, uuid, prop, max_length, read_perm, write_perm, comments)
        self.characteristics.append(char)
        return char

    def __str__(self):
        str_list = [
            'Serv name: %s' % self.name,
            'Serv type: 0x%04X' % self.type,
            'Serv uuid: 0x%04X' % self.uuid,
        ]
        for characteristic in self.characteristics:
            str_list.append(str(characteristic))
        if len(self.comments) > 0:
            str_list.append('Serv comment: %s' % self.comments)
        return '\n'.join(str_list)


class GattServices:
    def __init__(self, load_file=None):
        self.services = []
        if load_file:
            with open(load_file, 'r', encoding='utf-8') as file:
                self.load(file)
                file.close()

    def add_service(self, name, serv_type, uuid, comments=''):
        service = GattService(name, serv_type, uuid, comments)
        self.services.append(service)
        return service

    def load(self, file):
        return self.parse(json.load(file).get('services', {}))

    def parse(self, service_dict):
        for service in service_dict:
            assert isinstance(service, dict)
            serv_name = service.get('name', None)
            serv_type = service.get('type', None)
            serv_uuid = service.get('uuid', None)
            serv_comments = service.get('comments', '')
            if serv_name is not None and serv_type is not None and serv_uuid is not None:
                add_service = self.add_service(serv_name, serv_type, serv_uuid, serv_comments)
                serv_characteristics = service.get('characteristics', None)
                # characteristics
                if isinstance(serv_characteristics, list):
                    for characteristic in serv_characteristics:
                        assert isinstance(characteristic, dict)
                        char_name = characteristic.get('name', 'unknown')
                        char_uuid = characteristic.get('uuid', 0x0)
                        char_properties = characteristic.get('properties', 'R')
                        read_permission = write_permission = 'no_auth'
                        char_permission = characteristic.get('permission', None)  # read/write no_auth
                        if isinstance(char_permission, dict):
                            read_permission = characteristic.get('read', 'no_auth')
                            write_permission = characteristic.get('write', 'no_auth')
                        char_max_length = characteristic.get('max_length', 23)
                        char_comments = characteristic.get('comments', '')
                        add_char = add_service.add_characteristic(
                            char_name, char_uuid, Attribute.str2prop(char_properties), char_max_length,
                            Attribute.str2perm(read_permission), Attribute.str2perm(write_permission),
                            char_comments)
                        # values
                        char_values = characteristic.get('values', None)
                        if isinstance(char_values, list) and len(char_values) > 0:
                            for value in char_values:
                                assert isinstance(value, dict)
                                value_name = value.get('name', 'Unknown')
                                value_bit_length = value.get('bit_length', 0)
                                value_type = value.get('type', 'stream')
                                value_default = value.get('default', '0')
                                value_comments = value.get('comments', '')
                                add_char.add_value(value_name, value_bit_length, value_type, value_default, value_comments)
                        # descriptors
                        char_descriptors = characteristic.get('descriptors', None)
                        if isinstance(char_descriptors, list) and len(char_descriptors) > 0:
                            for desc in char_descriptors:
                                assert isinstance(desc, dict)
                                desc_uuid = desc.get('uuid', 'Unknown')
                                desc_prop = desc.get('properties', 'R/W')
                                desc_length = desc.get('max_length', 2)
                                read_permission = write_permission = 'no_auth'
                                desc_permission = desc.get('permission', None)
                                if isinstance(desc_permission, dict):
                                    read_permission = desc.get('read', 'no_auth')
                                    write_permission = desc.get('write', 'no_auth')
                                desc_comments = desc.get('comments', '')
                                add_char.add_descriptor(desc_uuid, Attribute.str2prop(desc_prop), desc_length,
                                                        Attribute.str2perm(read_permission),
                                                        Attribute.str2perm(write_permission), desc_comments)
            else:
                print('service add error: ', service)
        return self

    def __str__(self):
        return '\n'.join(str(service) for service in self.services)


def json_to_rw_service_init(json_path):
    header_service = '{0x2800, PERM(RD, ENABLE), 0, 0},'
    header_characteristic = '{0x2803, PERM(RD, ENABLE), 0, 0},'
    header_descriptor_cccd = '{0x2902, PERM(RD, ENABLE) | PERM(WRITE_REQ, ENABLE), 0, 2},'
    gatt_services = GattServices(json_path)

    def is_set(value, bit):
        return (value & (1 << bit)) == (1 << bit)

    def att2perm_str(attribute):
        print(type(attribute.prop), attribute.prop, attribute)
        if is_set(attribute.prop, attribute.PROP_BIT_READ):
            perm.append('PERM(RD, ENABLE)')
        if is_set(attribute.prop, attribute.PROP_BIT_WRITE):
            perm.append('PERM(WRITE_REQ, ENABLE)')
        if is_set(attribute.prop, attribute.PROP_BIT_WRITE_CMD):
            perm.append('PERM(WRITE_COMMAND, ENABLE)')
        if is_set(attribute.prop, attribute.PROP_BIT_NOTIFY):
            perm.append('PERM(NTF, ENABLE)')
        if is_set(attribute.prop, attribute.PROP_BIT_INDICATE):
            perm.append('PERM(IND, ENABLE)')
        if attribute.read_perm != attribute.PERM_NO_AUTH:
            perm.append('PERM(RP, UNAUTH)')
        if attribute.write_perm != attribute.PERM_NO_AUTH:
            perm.append('PERM(WP, UNAUTH)')
        return '|'.join(perm)

    def att2ext_perm_str(attribute):
        ext_perm = []
        if is_set(attribute.prop, GattCharacteristic.PROP_BIT_READ):
            ext_perm.append('PERM(RI, ENABLE)')
        return '|'.join(ext_perm) if len(ext_perm) > 0 else '0'

    func_names = []
    for service in gatt_services.services:
        assert isinstance(service, GattService)
        print('//----------------------------------------------')
        att_db_str = ''
        att_db_str += 'const struct attm_desc att_db[] =\n{\n'
        att_db_str += '\t// %s 0x%04X\n' % (service.name, service.uuid)
        att_db_str += '\t' + header_service + '\n'
        for characteristic in service.characteristics:
            assert isinstance(characteristic, GattCharacteristic)
            att_db_str += '\t// ' + characteristic.name + '\n'
            att_db_str += '\t' + header_characteristic + '\n'
            perm = []
            perm_str = att2perm_str(characteristic)
            ext_perm_str = att2ext_perm_str(characteristic)
            char_str = '{0x%04X, %s, %s, %d},' % (characteristic.uuid, perm_str, ext_perm_str, characteristic.max_length)
            att_db_str += '\t' + char_str + '\n'
            for descriptor in characteristic.descriptors:
                assert isinstance(descriptor, GattDescriptor)
                perm_str = att2perm_str(descriptor)
                ext_perm_str = att2ext_perm_str(descriptor)
                desc_str = '{0x%04X, %s, %s, %d},' % (descriptor.uuid, perm_str, ext_perm_str, descriptor.max_length)
                att_db_str += '\t' + desc_str + '\n'
        att_db_str += '};\n'

        serv_define = 'uint16_t start_hdl;\n' + \
                      'uint8_t status = attm_svc_create_db(&start_hdl, 0x%04X, NULL,\n' % service.uuid + \
                      '\t\tsizeof(att_db)/sizeof(att_db[0]), NULL, TASK_APP, att_db, PERM(SVC_AUTH, NO_AUTH));'
        func_name = 'service_%s_init' % service.name.replace(' ', '_').lower()
        func_names.append(func_name)
        serv_init_func = 'static void %s(void)\n{\n%s\n}\n' % \
                         (func_name, '\n'.join(['\t'+s for s in (att_db_str+serv_define).split('\n')]))
        print(serv_init_func)


if __name__ == '__main__':
    # with open('gatt_services.json', 'r', encoding='utf-8') as f:
    #     json_obj = json.load(f)
    #     services = json_obj['services']
    #     dump_services(services)
    #     f.close()

    # def camel_case_split(identifier):
    #     matches = re.finditer('.+?(?:(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|$)', identifier)
    #     return [m.group(0) for m in matches]
    # print(camel_case_split("NiceTryHa Ha"))
    json_to_rw_service_init('gatt_services.json')

