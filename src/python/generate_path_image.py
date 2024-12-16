import matplotlib.pyplot as plt
from matplotlib.patches import FancyArrowPatch, Rectangle
import colorsys
import ast


def read_coordinates(file_path):
    """Reads the coordinates from the input file."""
    with open(file_path, 'r') as file:
        content = file.read().strip()
    return ast.literal_eval(content)


def generate_path_picture(coordinates):
    """Generates a static picture of the path on a grid with dynamically adjusted size."""
    if not coordinates:
        print("No coordinates provided to generate picture.")
        return

    x_coords, y_coords = zip(*coordinates)  # Unpack x and y coordinates

    # Determine grid size based on 1-indexed coordinates
    min_x, max_x = min(x_coords), max(x_coords)
    min_y, max_y = min(y_coords), max(y_coords)

    # Calculate the range of coordinates
    x_range = max_x - min_x + 1
    y_range = max_y - min_y + 1

    # Dynamic figure sizing
    scaling_factor = 0.5  # Each unit in the grid adds this many inches
    base_size = 6  # Base size for a small grid
    width = base_size + x_range * scaling_factor
    height = base_size + y_range * scaling_factor

    plt.style.use('bmh')  # Use a clean style
    fig, ax = plt.subplots(figsize=(width, height), facecolor='#f0f0f0')
    ax.set_facecolor('#f9f9f9')

    ax.set_xlim(min_x - 1, max_x + 1)
    ax.set_ylim(min_y - 1, max_y + 1)
    ax.set_xticks(range(min_x - 1, max_x + 2))
    ax.set_yticks(range(min_y - 1, max_y + 2))
    ax.grid(True, linestyle='--', linewidth=0.5, color='#d0d0d0')
    ax.set_aspect('equal', adjustable='box')

    ax.set_title("Knight's Path Visualization", fontsize=16, fontweight='bold', pad=20)

    # Chessboard pattern
    for x in range(min_x, max_x + 1):
        for y in range(min_y, max_y + 1):
            color = '#ffffff' if (x + y) % 2 == 0 else '#808080'
            square = Rectangle((x - 0.5, y - 0.5), 1, 1, color=color, alpha=0.5, zorder=0)
            ax.add_patch(square)

    def generate_color_gradient(n):
        """Generate a color gradient for annotations."""
        colors = []
        for i in range(n):
            hue = i / n
            saturation = 0.8
            lightness = 0.5
            r, g, b = colorsys.hls_to_rgb(hue, lightness, saturation)
            colors.append((r, g, b))
        return colors

    color_gradient = generate_color_gradient(len(coordinates))

    # Annotate each point with a colored number
    for i, (y, x) in enumerate(coordinates):
        ax.text(
            x, y,
            f"{i + 1}",
            fontsize=10,
            fontweight='bold',
            ha='center',
            va='center',
            color=color_gradient[i],
            bbox=dict(facecolor='white', edgecolor=color_gradient[i], alpha=0.7, boxstyle='round,pad=0.3')
        )

    # Draw directional arrows
    for i in range(len(coordinates) - 1):
        arrow = FancyArrowPatch(
            (coordinates[i][1], coordinates[i][0]),
            (coordinates[i + 1][1], coordinates[i + 1][0]),
            arrowstyle='->',
            color='#333333',
            mutation_scale=15,
            linewidth=2,
            alpha=0.8
        )
        ax.add_patch(arrow)


    plt.tight_layout()
    plt.savefig("path_visualization.png", dpi=300)  # Higher DPI for clearer large images
#     plt.show()


def is_valid_knight_move(start, end):
    """Check if the move from start to end is a valid knight move."""
    dx = abs(start[0] - end[0])
    dy = abs(start[1] - end[1])
    return (dx == 2 and dy == 1) or (dx == 1 and dy == 2)


def check_coordinates(coordinates):
    """Check if the input coordinates are valid."""
    if len(coordinates) != len(set(coordinates)):
        return False
    for i in range(len(coordinates) - 1):
        if not is_valid_knight_move(coordinates[i], coordinates[i + 1]):
            return False
    return True


if __name__ == "__main__":
    file_path = "../../path.txt"
    try:
        coordinates = read_coordinates(file_path)
        isValid = check_coordinates(coordinates)
        if not isValid:
            print("Invalid input coordinates.")
            exit(1)
        if isValid:
            print("Valid input coordinates.")
        generate_path_picture(coordinates)
    except FileNotFoundError:
        print(f"File not found: {file_path}")
    except Exception as e:
        print(f"An error occurred: {e}")
