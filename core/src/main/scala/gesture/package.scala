package object gesture {

  type Vec2d = (Double, Double)

  implicit class RichVec2d(v: Vec2d) {

    def +(other: Vec2d) = (v._1 + other._1, v._2 + other._2)

    def -(other: Vec2d) = (v._1 - other._1, v._2 - other._2)

    def distanceTo(other: Vec2d) = {
      val dx = other._1 - v._1
      val dy = other._2 - v._2
      math.sqrt(dx * dx + dy * dy)
    }
  }

}
