package simulationProgram.util;

import java.util.ArrayList;

import commonInterface.Captor;
import simulationProgram.simMap.Map;
import simulationProgram.simMap.Obstacle;
import simulationProgram.simRobot.SimRobot;

public abstract class Radar {
	private double mapWidth;
	private double mapHeight;
	private double xRobot;
	private double yRobot;
	private double a;
	private double b;
	private double robotSize;
	private double alphaOrientation;
	private ArrayList<Captor> robotCaptors;
	private double edgeDistance;

	public boolean crash = false;
	// il y a l'arraylist capteur, qu'il faut recuperer du robot

	public Object radar(ArrayList<Obstacle> ObstaclesList, SimRobot titi, Map map) {
		mapWidth = map.getWidth();
		mapHeight = map.getHeight();
		xRobot = titi.getXPos();
		yRobot = titi.getYPos();

		robotSize = titi.getRobotSize();
		alphaOrientation = titi.getAlphaOrientation();
		robotCaptors = titi.getRobotCaptors();

		if (xRobot > mapWidth - robotSize || xRobot < robotSize || yRobot > mapHeight - robotSize
				|| yRobot < mapHeight) {
			// capteurs.set(0, (double) -1);
			for (Captor captor : robotCaptors)
				captor.setDistance(0);

			return null;
		}

		for (Obstacle obstacle : ObstaclesList) {
			if ((obstacle.getXPos() - xRobot) * (obstacle.getXPos() - xRobot)
					+ (obstacle.getYPos() - yRobot) * (obstacle.getYPos() - yRobot) > (obstacle.getRadius() + robotSize)
							* (obstacle.getRadius() + robotSize))
				// le robot n'est pas dans l'obstacle
				// d�termination de la distance des capteurs � l'obstacle
				System.out.printf("lol");

			// equation de la droite de vision du capteur
			a = Math.tan(alphaOrientation);
			b = (yRobot - xRobot);
			
			for (int j = 1; j < 6; j++) {

				ArrayList<Double> solutions = new ArrayList<Double>();
				solutions = getIntersectionPoints(a, b, obstacle.getXPos(), obstacle.getYPos(), obstacle.getRadius());
				edgeDistance = getEdgeDistance(a, b, alphaOrientation - 160 / 2 + 160 / 5 * j, xRobot, yRobot);// alpha-160/2+160/5*j
				// est l'angle du
				// capteur J

				if (solutions.size() == 0) {
					// pas de solutions avec les obstacles, regarder le bord de terrain
					robotCaptors.get(j).setDistance(edgeDistance);
					// return null; //Il faut assigner la distance au 5 capteurs non?
				} else if (solutions.size() == 2) {
					// 1seule solution : v�rifier juste la demi-droite
					if (0 < alphaOrientation && alphaOrientation < Math.PI) {
						if (solutions.get(1) > yRobot) {
							robotCaptors.get(j).setDistance(
									Math.min(edgeDistance, ((xRobot - solutions.get(0)) * (xRobot - solutions.get(0))
											+ (yRobot - solutions.get(1)) * (yRobot - solutions.get(1)))));
							return null;
						} else
							return null;
					}

					else {
						if (solutions.get(1) < yRobot) {
							robotCaptors.get(j).setDistance(
									Math.min(edgeDistance, ((xRobot - solutions.get(0)) * (xRobot - solutions.get(0))
											+ (yRobot - solutions.get(1)) * (yRobot - solutions.get(1)))));
							return null;
						} else
							return null;
					}
				}

				else {
					// 2 solutions
					if (0 < alphaOrientation && alphaOrientation < Math.PI) {
						if (solutions.get(1) > yRobot) {
							solutions.remove(3);
							solutions.remove(2);

						}
						if (0 < alphaOrientation && alphaOrientation < Math.PI) {
							if (solutions.get(1) > yRobot && solutions.get(3) > yRobot) {
								robotCaptors.get(j)
										.setDistance(Math.min(edgeDistance, Math.min(
												((xRobot - solutions.get(0)) * (xRobot - solutions.get(0))
														+ (yRobot - solutions.get(1)) * (yRobot - solutions.get(1))),
												((xRobot - solutions.get(2)) * (xRobot - solutions.get(2))
														+ (yRobot - solutions.get(3)) * (yRobot - solutions.get(3))))));
								return null;
							} else if (solutions.get(1) > yRobot && solutions.get(3) < yRobot) {
								robotCaptors.get(j)
										.setDistance(Math.min(edgeDistance,
												((xRobot - solutions.get(0)) * (xRobot - solutions.get(0))
														+ (yRobot - solutions.get(1)) * (yRobot - solutions.get(1)))));
								return null;
							} else if (solutions.get(1) < yRobot && solutions.get(3) > yRobot) {
								robotCaptors.get(j)
										.setDistance(Math.min(edgeDistance,
												((xRobot - solutions.get(2)) * (xRobot - solutions.get(3))
														+ (yRobot - solutions.get(2)) * (yRobot - solutions.get(3)))));
								return null;
							} else if (solutions.get(1) < yRobot && solutions.get(3) < yRobot) {
								return null;
							}
						} else {
							if (solutions.get(1) > yRobot && solutions.get(3) > yRobot) {
								return null;
							} else if (solutions.get(1) > yRobot && solutions.get(3) < yRobot) {
								robotCaptors.get(j)
										.setDistance(Math.min(edgeDistance,
												((xRobot - solutions.get(2)) * (xRobot - solutions.get(3))
														+ (yRobot - solutions.get(2)) * (yRobot - solutions.get(3)))));
								return null;
							} else if (solutions.get(1) < yRobot && solutions.get(3) > yRobot) {
								robotCaptors.get(j)
										.setDistance(Math.min(edgeDistance,
												((xRobot - solutions.get(0)) * (xRobot - solutions.get(0))
														+ (yRobot - solutions.get(1)) * (yRobot - solutions.get(1)))));
								return null;
							} else if (solutions.get(1) < yRobot && solutions.get(3) < yRobot) {
								robotCaptors.get(j)
										.setDistance(Math.min(edgeDistance, Math.min(
												((xRobot - solutions.get(0)) * (xRobot - solutions.get(0))
														+ (yRobot - solutions.get(1)) * (yRobot - solutions.get(1))),
												((xRobot - solutions.get(2)) * (xRobot - solutions.get(2))
														+ (yRobot - solutions.get(3)) * (yRobot - solutions.get(3))))));
								return null;
							}

							else {
								if (solutions.get(1) < yRobot) {
									robotCaptors.get(j).setDistance(Math.min(edgeDistance,
											((xRobot - solutions.get(0)) * (xRobot - solutions.get(0))
													+ (yRobot - solutions.get(1)) * (yRobot - solutions.get(1)))));
									return null;
								} else
									return null;
							}
						}

					}

					else {
						// le robot est dans l'obstacle
						// capteurs.set(0, (double) -1);
						robotCaptors.get(j).setDistance(0);
						return null;

					}
				}
			}
		}
		return null;

	}

	public double getEdgeDistance(double a, double b, double alpharadar, double xrobot, double yrobot) {
		double xintersection1;
		double xintersection2;
		double yintersection3;
		double yintersection4;

		double d1;
		double d2;
		double d3;
		double d4;

		xintersection1 = (b + mapHeight) / a;// haut
		xintersection2 = b / a;// axe des abscisses
		yintersection3 = a * mapWidth + b;// bord droit
		yintersection4 = b;// bord gauche, axe des ordonn�es

		if (0 < xintersection1 && xintersection1 < mapHeight) {
			d1 = Math.cbrt(
					(mapHeight - yrobot) * (mapHeight - yrobot) + (xrobot - xintersection1) * (xrobot - xintersection1));
		} else
			d1 = -1;
		if (0 < xintersection2 && xintersection2 < mapWidth) {
			d2 = Math.cbrt((yrobot) * (yrobot) + (xrobot - xintersection2) * (xrobot - xintersection2));
		} else
			d2 = -1;
		if (0 < yintersection3 && yintersection3 < mapHeight) {
			d3 = Math.cbrt(
					(yintersection3 - yrobot) * (yintersection3 - yrobot) + (xrobot - mapWidth) * (xrobot - mapWidth));
		} else
			d3 = -1;
		if (0 < yintersection4 && yintersection4 < mapHeight) {
			d4 = Math.cbrt((yintersection4 - yrobot) * (yintersection4 - yrobot) + (xrobot) * (xrobot));
		} else
			d4 = -1;

		if (0 <= alphaOrientation && alphaOrientation < Math.PI / 2 && d1 == -1) {
			return d3;
		} else if (0 <= alphaOrientation && alphaOrientation < Math.PI / 2 && d3 == -1) {
			return d1;
		} else if (Math.PI / 2 <= alphaOrientation && alphaOrientation < Math.PI && d1 == -1) {
			return d4;
		} else if (Math.PI / 2 <= alphaOrientation && alphaOrientation < Math.PI && d4 == -1) {
			return d1;
		} else if (Math.PI <= alphaOrientation && alphaOrientation < 3 * Math.PI / 2 && d2 == -1) {
			return d3;
		} else if (Math.PI <= alphaOrientation && alphaOrientation < 3 * Math.PI / 2 && d3 == -1) {
			return d2;
		} else if (3 * Math.PI / 2 <= alphaOrientation && alphaOrientation < 2 * Math.PI && d2 == -1) {
			return d4;
		} else
			return d2;

	}

	public ArrayList<Double> getIntersectionPoints(double a, double b, double cx, double cy, double r) {
		ArrayList<Double> lst = new ArrayList<Double>();

		double A = 1 + a;
		double B = 2 * (-cx + a * b - a * cy);
		double C = cx * cx + cy * cy + b * b - 2 * b * cy - r * r;
		double delta = B * B - 4 * A * C;

		if (delta > 0) {
			double x = (-B - Math.cbrt(delta)) / (2 * A);
			double y = a * x + b;
			lst.add(x);
			lst.add(y);

			x = (-B + Math.cbrt(delta)) / (2 * A);
			y = a * x + b;
			lst.add(x);
			lst.add(y);

		} else if (delta == 0) {
			double x = -B / (2 * A);
			double y = a * x + b;

			lst.add(x);
			lst.add(y);
		}

		return lst;
	}

}
