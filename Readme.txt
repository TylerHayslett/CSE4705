The data relates to an inverse dynamic problem for a seven degree-of-freedom SARCOS anthropomorphic robot arm. The task is to map from a 21-dimensional input
features (7 joint positions, 7 joint velocities, 7 joint accelerations) to the corresponding 7 joint torques. In this experiment, we only construct a regression
model to map the 21-dimensional input to one joint torque, the first joint torque.

References: [1] Zhang, Yu, and Dit-Yan Yeung. "A Convex Formulation for Learning Task Relationships in Multi-Task Learning." [2] a website
http://www.gaussianprocess.org/gpml/data/

----------------------------
----------------------------
Training sample size : 2000
    Training data is in Sarcos_Data1_train.xlsx. Each row is an example and each column is a feature. The features in each row are used to describe the corresponding example.

Test sample size : 500
    Test data is in Sarcos_Data1_test.xlsx.

----------------------------
----------------------------
Number of features : 21
    The 21 features include 7 joint positions, 7 point velocities and 7 joint accelerations.

----------------------------
----------------------------
Labels : The first joint torque of the total 7 torques is a continuous (real-valued) variable.


----------------------------
----------------------------
File format:
    For both training and test data files, the first row gives the feature names. The first column gives the example ID which is not a feature, and you should not
    use it when training your model. The second column gives the label for each image (or patient) as explained above, and the values are real numbers.  This is the target variable that you
    want to predict based on other features of each patient.


Your task is to train a regression model using a machine learning method studied in our class. You should only build your model from the training data, and then
test your model on the test data. You can either write your own codes to implement the method or download some existing machine learning packages. If you decide to
use downloaded package, please state the source of your download so TA can understand. If you decide to use a downloaded package, you might need to adapt it to the
given data. You are welcome to also try to figure out some other methods not studied in our class that perform better than our studied techniques. This will bring
extra credits of 5 points additional to the 40 total points. You need to make sure a in-class-studied technique is used first to compare with your other methods.
