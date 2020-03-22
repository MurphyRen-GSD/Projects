class VendingMachine:
    v = 0
    def __init__(self):
        self.soda = JunkDrink(self)

class JunkDrink:
    v = 0
    def __init__(self, machine):
        self.machine = machine
        self.machine.v = self.machine.v + 1
        machine.v = machine.v + 1
        self.v = self.v + 1