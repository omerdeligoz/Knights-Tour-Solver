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

    # Dynamic figure sizing
    # Calculate the range of coordinates
    x_range = max_x - min_x + 1
    y_range = max_y - min_y + 1

    # Define base figure size and scaling factor
    base_size = 6  # Base figure size in inches
    max_size = 15  # Maximum figure size to prevent extremely large images
    min_size = 4  # Minimum figure size

    # Calculate figure size based on coordinate range
    width = min(max(min_size, base_size * (x_range / 10)), max_size)
    height = min(max(min_size, base_size * (y_range / 10)), max_size)

    # Set up the plot with improved styling
    plt.style.use('bmh')  # Use a built-in Matplotlib style
    fig, ax = plt.subplots(figsize=(width, height), facecolor='#f0f0f0')
    ax.set_facecolor('#f9f9f9')

    # Configure axes
    ax.set_xlim(min_x - 1, max_x + 1)
    ax.set_ylim(min_y - 1, max_y + 1)
    ax.set_xticks(range(min_x - 1, max_x + 2))
    ax.set_yticks(range(min_y - 1, max_y + 2))
    ax.grid(True, linestyle='--', linewidth=0.5, color='#d0d0d0')
    ax.set_aspect('equal', adjustable='box')

    # Enhanced title and labels
    ax.set_title("Path Visualization", fontsize=16, fontweight='bold', pad=20)
    ax.set_xlabel("X Coordinate", fontsize=12)
    ax.set_ylabel("Y Coordinate", fontsize=12)

    # Draw the chessboard pattern with softer colors
    for x in range(min_x, max_x + 1):
        for y in range(min_y, max_y + 1):
            color = '#ffffff' if (x + y) % 2 == 0 else '#808080'
            square = Rectangle((x - 0.5, y - 0.5), 1, 1, color=color, alpha=0.5, zorder=0)
            ax.add_patch(square)

    # Prepare for color gradient
    def generate_color_gradient(n):
        """Generate a color gradient for annotations."""
        colors = []
        for i in range(n):
            # Create a smooth color transition
            hue = i / n
            saturation = 0.8
            lightness = 0.5
            r, g, b = colorsys.hls_to_rgb(hue, lightness, saturation)
            colors.append((r, g, b))
        return colors

    color_gradient = generate_color_gradient(len(coordinates))

    # Plot the path
    ax.plot(x_coords, y_coords, marker='o', color='#1E90FF', linestyle='-', linewidth=2, markersize=10, alpha=0.7)
    ax.plot([x_coords[-1]], [y_coords[-1]], marker='o', color='#FF4500', markersize=15)

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
            color='#555555',
            mutation_scale=15,
            linewidth=1.5,
            alpha=0.6
        )
        ax.add_patch(arrow)

    # Enhanced figure styling
    plt.tight_layout()

    # Save the picture
    plt.savefig("path_visualization.png")
    plt.show()


def is_valid_knight_move(start, end):
    """Check if the move from start to end is a valid knight move."""
    dx = abs(start[0] - end[0])
    dy = abs(start[1] - end[1])
    return (dx == 2 and dy == 1) or (dx == 1 and dy == 2)


def check_coordinates(coordinates):
    """Check if the input coordinates are valid."""
    # Check if each coordinate exists only once
    if len(coordinates) != len(set(coordinates)):
        return False

    # Check if each consecutive coordinate forms a valid knight move
    for i in range(len(coordinates) - 1):
        if not is_valid_knight_move(coordinates[i], coordinates[i + 1]):
            return False

    return True


if __name__ == "__main__":
    # File path input
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
