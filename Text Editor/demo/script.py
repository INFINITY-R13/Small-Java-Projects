import math
from typing import List

# A quick Python sample to showcase syntax highlighting in AuraEdit

class Particle:
    def __init__(self, x: float, y: float, speed: float = 1.0):
        self.x = x
        self.y = y
        self.speed = speed

    def move(self, angle_deg: float) -> None:
        radians = math.radians(angle_deg)
        self.x += self.speed * math.cos(radians)
        self.y += self.speed * math.sin(radians)

    def __repr__(self) -> str:
        return f"Particle(x={self.x:.2f}, y={self.y:.2f})"

def run_simulation(steps: int = 5) -> List[Particle]:
    particle = Particle(0.0, 0.0, speed=2.5)
    history = [particle]
    for step in range(steps):
        particle.move(angle_deg=45.0)
        history.append(particle)
    return history

if __name__ == "__main__":
    result = run_simulation()
    print("Simulation completed successfully:", len(result), "points")
